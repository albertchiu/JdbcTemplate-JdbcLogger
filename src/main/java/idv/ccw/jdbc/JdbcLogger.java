package idv.ccw.jdbc;

import java.sql.Timestamp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class JdbcLogger {
    private static final Log logger = LogFactory.getLog(JdbcLogger.class);

    @Before("execution(* org.springframework.jdbc.core.JdbcTemplate.*(String, ..))")
    public void log(JoinPoint joinPoint) throws Throwable {

        if (logger.isDebugEnabled()) {
            Object[] methodArgs = joinPoint.getArgs(), sqlArgs = null;
            String sqlStatement = methodArgs[0].toString();

            // find the SQL parameters
            for (int i = 1, n = methodArgs.length; i < n; i++) {
                Object arg = methodArgs[i];
                if (arg instanceof Object[]) {
                    sqlArgs = (Object[]) arg;
                    break;
                }
            }

            logger.debug(sqlArgs == null ? sqlStatement : fillParameters(sqlStatement, sqlArgs));
        }
    }

    private String fillParameters(String sql, Object[] params) {
        for (Object param : params) {
            int index = sql.indexOf("?");

            if (index != -1) {
                if (param == null) {
                    sql = sql.substring(0, index) + "null" + sql.substring(index + 1);
                } else if (param instanceof String) {
                    sql = sql.substring(0, index) + "'" + (String) param + "'" + sql.substring(index + 1);
                } else if (param instanceof Boolean) {
                    int boolInt = ((Boolean) param).booleanValue() ? 1 : 0;
                    sql = sql.substring(0, index) + boolInt + sql.substring(index + 1);
                } else if (param instanceof Timestamp) {
                    sql = sql.substring(0, index) + "to_timestamp('" + String.format("%1$tF %1$tT.%1$tL", param)
                            + "','YYYY-MM-DD HH24:MI:SS.FF3')" + sql.substring(index + 1);
                } else if (param instanceof java.sql.Date || param instanceof java.util.Date) {
                    sql = sql.substring(0, index) + "to_date('" + String.format("%1$tF %1$tT", param)
                            + "','YYYY-MM-DD HH24:MI:SS')" + sql.substring(index + 1);
                } else {
                    sql = sql.substring(0, index) + param.toString() + sql.substring(index + 1);
                }
            }
        }

        return sql;
    }
}