package com.aorkado;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.hyperledger.fabric.shim.ChaincodeBase;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ResponseUtils;

public class MainChaincode extends ChaincodeBase {

  private static final String OBS_CONSOLE_LOGGER = "OBS_CONSOLE_LOGGER";

  public static void main(String[] args) {
    initLog4J2();
    new MainChaincode().start(args);
  }

  @Override
  public Response init(ChaincodeStub stub) {
    rootLogger().info("rootLogger => init");
    obsConsoleLogger().info("obsConsoleLogger => init");
    return ResponseUtils.newSuccessResponse();
  }

  @Override
  public Response invoke(ChaincodeStub stub) {
    rootLogger().info("rootLogger => invoke");
    obsConsoleLogger().info("obsConsoleLogger => invoke");
    return ResponseUtils.newSuccessResponse();
  }

  private Logger rootLogger() {
    return LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);
  }

  private Logger obsConsoleLogger() {
    return LogManager.getLogger(OBS_CONSOLE_LOGGER);
  }

  private static void initLog4J2() {
    LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
    Configuration config = ctx.getConfiguration();

    PatternLayout layout = PatternLayout.newBuilder()
        .withConfiguration(config)
        .withPattern("%d{ISO8601} %-5p (%t) [%c(%M:%L)] %m%n")
        .build();

    Appender appender = ConsoleAppender.newBuilder()
        .setConfiguration(config)
        .withName("CONSOLE")
        .withLayout(layout)
        .build();

    appender.start();

    config.addAppender(appender);

    // EDITING ROOT LOGGER
    LoggerConfig rootLoggerConfig = config.getRootLogger();
    rootLoggerConfig.setAdditive(true);
    rootLoggerConfig.setLevel(Level.INFO);
    rootLoggerConfig.addAppender(appender, null, null);

    // CREATE A NEW LOGGER
    AppenderRef ref = AppenderRef.createAppenderRef(OBS_CONSOLE_LOGGER, null, null);
    AppenderRef[] refs = new AppenderRef[]{ref};

    LoggerConfig obsConsoleLoggerConfig = LoggerConfig
        .createLogger(true, Level.INFO, OBS_CONSOLE_LOGGER, "true", refs, null, config, null);
    obsConsoleLoggerConfig.addAppender(appender, null, null);
    config.addLogger(OBS_CONSOLE_LOGGER, obsConsoleLoggerConfig);

    // NEED TO UPDATE LOGGERS ON CONTEXT
    ctx.updateLoggers();
  }
}