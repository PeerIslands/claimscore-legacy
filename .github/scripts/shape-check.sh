#!/usr/bin/env bash
# Shape check for the ClaimsCore legacy fixture. Verifies the repository still
# has the shape the modernization demo depends on (table count, PL/SQL package,
# layer inventory, descriptors, the N+1 pattern, file sizes, README wording).
# Portable across GNU (CI) and BSD (macOS): bash, grep, awk, find, wc only.
set -euo pipefail
cd "$(dirname "$0")/../.."

failed=0
ok() { echo "ok - $1"; }
fail() { echo "FAIL - $1"; failed=1; }

# (1) exactly 47 CREATE TABLE lines in the DDL
ddl=db/ddl/oracle-schema.sql
if [ -f "$ddl" ]; then
  n=$(grep -c '^CREATE TABLE' "$ddl" || true)
  if [ "$n" -eq 47 ]; then
    ok "ddl has 47 CREATE TABLE lines"
  else
    fail "ddl has $n CREATE TABLE lines, expected 47"
  fi
else
  fail "$ddl missing"
fi

# (2) PL/SQL package spec and body
pks=db/packages/PKG_CLAIMS.pks
pkb=db/packages/PKG_CLAIMS.pkb
if [ -f "$pks" ] && grep -q 'PROCEDURE ADJUDICATE_CLAIM' "$pks"; then
  ok "PKG_CLAIMS.pks declares ADJUDICATE_CLAIM"
else
  fail "PKG_CLAIMS.pks missing or does not declare ADJUDICATE_CLAIM"
fi
if [ -f "$pkb" ] && grep -q 'DEDUCTIBLE' "$pkb" && grep -q 'COVERAGE_LIMIT' "$pkb"; then
  ok "PKG_CLAIMS.pkb mentions DEDUCTIBLE and COVERAGE_LIMIT"
else
  fail "PKG_CLAIMS.pkb missing or lacks DEDUCTIBLE / COVERAGE_LIMIT"
fi

# (3) layer inventory
for layer in struts ejb dao dto util; do
  dir="src/main/java/com/claimscore/$layer"
  n=0
  if [ -d "$dir" ]; then
    n=$(find "$dir" -name '*.java' -type f | wc -l | tr -d ' ')
  fi
  if [ "$n" -ge 4 ]; then
    ok "$layer has $n java files"
  else
    fail "$layer has $n java files, expected >= 4"
  fi
done
total_java=0
if [ -d src/main/java ]; then
  total_java=$(find src/main/java -name '*.java' -type f | wc -l | tr -d ' ')
fi
if [ "$total_java" -ge 30 ]; then
  ok "src/main/java has $total_java java files"
else
  fail "src/main/java has $total_java java files, expected >= 30"
fi

# (4) deployment descriptors
for d in src/main/webapp/WEB-INF/web.xml \
         src/main/webapp/WEB-INF/struts-config.xml \
         src/main/resources/META-INF/ejb-jar.xml \
         src/main/resources/META-INF/weblogic-ejb-jar.xml; do
  if [ -f "$d" ]; then
    ok "$d exists"
  else
    fail "$d missing"
  fi
done

# (5) the N+1 pattern: a for-loop over claims followed later by prepareStatement
impl=src/main/java/com/claimscore/dao/ClaimDAOImpl.java
if [ -f "$impl" ] && awk '
  /for \(.*claims.*\)/ { seen = 1 }
  seen && /prepareStatement/ { found = 1 }
  END { exit found ? 0 : 1 }' "$impl"; then
  ok "ClaimDAOImpl has the N+1 loop"
else
  fail "ClaimDAOImpl missing or has no for(...claims...) followed by prepareStatement"
fi

# (6) file sizes and extension allowlist; (8) total size
allow=" java jsp xml properties sql pks pkb trg md sh yml txt gitignore "
list_files() {
  if git rev-parse --is-inside-work-tree >/dev/null 2>&1; then
    git ls-files --cached --others --exclude-standard
  else
    find . -type f -not -path './.git/*' -not -path './target/*' -not -path './aidlc-out/*' | sed 's|^\./||'
  fi
}
big=0
badext=0
total=0
while IFS= read -r f; do
  [ -f "$f" ] || continue
  size=$(wc -c < "$f" | tr -d ' ')
  total=$((total + size))
  if [ "$size" -gt 65536 ]; then
    echo "  oversize: $f ($size bytes)"
    big=1
  fi
  base=${f##*/}
  if [ "$base" != "LICENSE" ]; then
    case "$base" in
      *.*) ext=${base##*.} ;;
      *)   ext="" ;;
    esac
    case "$allow" in
      *" $ext "*) ;;
      *) echo "  extension not allowed: $f"; badext=1 ;;
    esac
  fi
done < <(list_files)
if [ "$big" -eq 0 ]; then ok "no file over 65536 bytes"; else fail "a file exceeds 65536 bytes"; fi
if [ "$badext" -eq 0 ]; then ok "all file extensions in allowlist"; else fail "file extension outside allowlist"; fi

# (7) README first 20 lines say synthetic
if [ -f README.md ] && awk '
  NR <= 20 && tolower($0) ~ /synthetic/ { found = 1 }
  NR > 20 { exit }
  END { exit found ? 0 : 1 }' README.md; then
  ok "README says synthetic in the first 20 lines"
else
  fail "README missing or does not say synthetic in the first 20 lines"
fi

# (8) total size under 300 KB
if [ "$total" -lt 307200 ]; then
  ok "total size $total bytes under 300 KB"
else
  fail "total size $total bytes, expected under 300 KB"
fi

if [ "$failed" -ne 0 ]; then
  echo "shape check FAILED"
  exit 1
fi
echo "shape check passed"
