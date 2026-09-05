package com.claimscore.struts;

import java.security.MessageDigest;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.action.DynaActionForm;

import com.claimscore.util.Constants;
import com.claimscore.util.OracleConnectionFactory;

/**
 * Authenticates against APP_USERS and seeds the HTTP session.
 *
 * @author dkowalczyk
 * $Id: LoginAction.java,v 1.8 2010/03/29 09:12:47 dkowalczyk Exp $
 */
public class LoginAction extends Action {

    private static final Logger log = Logger.getLogger(LoginAction.class);

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        if ("logout".equals(request.getParameter("method"))) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            return mapping.findForward("failure");
        }
        DynaActionForm loginForm = (DynaActionForm) form;
        String username = (String) loginForm.get("username");
        String password = (String) loginForm.get("password");
        if (username == null || username.trim().length() == 0) {
            return mapping.findForward("failure");
        }
        username = username.trim();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = OracleConnectionFactory.getConnection();
            ps = conn.prepareStatement("SELECT USER_ID, USERNAME, PASSWORD_HASH, FULL_NAME, STATUS_CD, "
                    + "FAILED_ATTEMPTS FROM APP_USERS WHERE USERNAME = ?");
            ps.setString(1, username);
            rs = ps.executeQuery();
            long userId = -1;
            String storedHash = null;
            String statusCode = null;
            if (rs.next()) {
                userId = rs.getLong("USER_ID");
                storedHash = rs.getString("PASSWORD_HASH");
                statusCode = rs.getString("STATUS_CD");
            }
            // FIXME INC-3902: no lockout on FAILED_ATTEMPTS, the counter is only ever displayed
            if (userId > 0 && "A".equals(statusCode) && md5Hex(password).equals(storedHash)) {
                HttpSession session = request.getSession(true);
                session.setAttribute(Constants.SESSION_USER_KEY, username);
                session.setAttribute(Constants.SESSION_ROLES_KEY, loadRoles(conn, userId));
                touchUser(conn, "UPDATE APP_USERS SET LAST_LOGIN_DT = SYSDATE, FAILED_ATTEMPTS = 0 WHERE USER_ID = ?",
                        userId);
                log.info("User " + username + " logged in");
                return mapping.findForward("success");
            }
            if (userId > 0) {
                touchUser(conn, "UPDATE APP_USERS SET FAILED_ATTEMPTS = FAILED_ATTEMPTS + 1 WHERE USER_ID = ?", userId);
            }
            log.warn("Failed login for " + username);
        } finally {
            OracleConnectionFactory.close(rs, ps, conn);
        }
        ActionErrors errors = new ActionErrors();
        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.login.invalid"));
        saveErrors(request, errors);
        return mapping.findForward("failure");
    }

    private List<String> loadRoles(Connection conn, long userId) throws SQLException {
        List<String> roles = new ArrayList<String>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement("SELECT ROLE_CD FROM APP_USER_ROLES WHERE USER_ID = ?");
            ps.setLong(1, userId);
            rs = ps.executeQuery();
            while (rs.next()) {
                roles.add(rs.getString("ROLE_CD"));
            }
        } finally {
            OracleConnectionFactory.close(rs, ps, null);
        }
        return roles;
    }

    private void touchUser(Connection conn, String sql, long userId) throws SQLException {
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setLong(1, userId);
            ps.executeUpdate();
        } finally {
            OracleConnectionFactory.close(null, ps, null);
        }
    }

    private static String md5Hex(String s) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest((s == null ? "" : s).getBytes());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < digest.length; i++) {
            String hex = Integer.toHexString(0xff & digest[i]);
            sb.append(hex.length() == 1 ? "0" + hex : hex);
        }
        return sb.toString();
    }
}
