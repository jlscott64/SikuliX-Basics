/*
 * Copyright 2010-2013, Sikuli.org
 * Released under the MIT License.
 *
 * modified RaiMan 2013
 */
package org.sikuli.basics;

import java.io.File;
import java.util.Arrays;
import org.apache.commons.cli.CommandLine;

/**
 * Contains the main class
 */
public class SikuliScript {

  private static final String me = "SikuliScript: ";
  /**
   * The ScriptRunner that is used to execute the script.
   */
  private static IScriptRunner runner;
  private static File imagePath;
  private static Boolean runAsTest;

  private static boolean isRunningInteractive = false;
  public static void runningInteractive() {
    isRunningInteractive = true;
    SikuliX.displaySplash(null);
  }
  
  public static boolean getRunningInteractive() {
    return isRunningInteractive;
  }
  
  private static boolean isRunningScript = false;
  
  /**
   * Main method
   *
   * @param args passed arguments
   */
  public static void main(String[] args) {
    
    if (isRunningScript) {
      System.out.println("[error] SikuliScript: can only run one at a time!");
      return;
    }
    
    isRunningScript = true;

    SikuliX.displaySplash(args);

    if (args != null && args.length > 1 && args[0].startsWith("-testSetup")) {
      runner = SikuliX.getScriptRunner(args[1], null, args);
      if (runner == null) {
        args[0] = null;
      } else {
        String[] stmts = new String[0];
        if (args.length > 2) {
          stmts = new String[args.length - 2];
          for (int i = 0; i < stmts.length; i++) {
            stmts[i] = args[i+2];
          }
        }
        if (0 != runner.runScript(null, null, stmts, null)) {
          args[0] = null;
        }
      }
      isRunningScript = false;
      return;
    }
    
    CommandArgs cmdArgs = new CommandArgs("SCRIPT");
    CommandLine cmdLine = cmdArgs.getCommandLine(CommandArgs.scanArgs(args));
    String cmdValue;
    
    if (cmdLine == null || cmdLine.getOptions().length == 0) {
      Debug.error("Did not find any valid option on command line!");
      cmdArgs.printHelp();
      System.exit(1);
    }

    if (cmdLine.hasOption(CommandArgsEnum.HELP.shortname())) {
      cmdArgs.printHelp();
      if (runner != null) {
        System.out.println(runner.getCommandLineHelp());
      }
      System.exit(1);
    }

    if (cmdLine.hasOption(CommandArgsEnum.LOGFILE.shortname())) {
      cmdValue = cmdLine.getOptionValue(CommandArgsEnum.LOGFILE.longname());
      if (!Debug.setLogFile(cmdValue == null ? "" : cmdValue)) {
        System.exit(1);
      }
    }

    if (cmdLine.hasOption(CommandArgsEnum.USERLOGFILE.shortname())) {
      cmdValue = cmdLine.getOptionValue(CommandArgsEnum.USERLOGFILE.longname());
      if (!Debug.setUserLogFile(cmdValue == null ? "" : cmdValue)) {
        System.exit(1);
      }
    }

    if (cmdLine.hasOption(CommandArgsEnum.DEBUG.shortname())) {
      cmdValue = cmdLine.getOptionValue(CommandArgsEnum.DEBUG.longname());
      if (cmdValue == null) {
        Debug.setDebugLevel(3);
        Settings.LogTime = true;
        if (!Debug.isLogToFile()) {
          Debug.setLogFile("");
        }
      } else {
        Debug.setDebugLevel(cmdValue);
      }      
    }

    Settings.setArgs(cmdArgs.getUserArgs(), cmdArgs.getSikuliArgs());
    Debug.log(3, me + "CmdOrg: " + System.getenv("SIKULI_COMMAND"));
    Settings.showJavaInfo();
    Settings.printArgs();

//TODO    if (cmdLine.hasOption(CommandArgsEnum.IMAGEPATH.shortname())) {
    if (false) {
//      imagePath = getScriptRunner(cmdLine.getOptionValue(CommandArgsEnum.IMAGEPATH.longname()), null, args);
    } else {
      imagePath = null;
    }

    // select script runner and/or start interactive session
    // option is overloaded - might specify runner for -r/-t
    if (cmdLine.hasOption(CommandArgsEnum.INTERACTIVE.shortname())) {
      System.out.println(String.format(
              "SikuliX Package Build: %s %s", Settings.getVersionShort(), RunSetup.timestampBuilt));
      int exitCode = 0;
      if (runner == null) {
        String givenRunnerName = cmdLine.getOptionValue(CommandArgsEnum.INTERACTIVE.longname());
        if (givenRunnerName == null) {
          runner = SikuliX.getScriptRunner("jython", null, args);
        } else {
          runner = SikuliX.getScriptRunner(givenRunnerName, null, args);
          if (runner == null) {
            System.exit(1);
          }
        }
      }
      if (!cmdLine.hasOption(CommandArgsEnum.RUN.shortname())
              && !cmdLine.hasOption(CommandArgsEnum.TEST.shortname())) {
        exitCode = runner.runInteractive(cmdArgs.getUserArgs());
        runner.close();
        SikuliX.endNormal(exitCode);
      }
    }

    String givenScriptName = null;
    runAsTest = false;

    if (cmdLine.hasOption(CommandArgsEnum.LOAD.shortname())) {
      String loadScript = FileManager.slashify(cmdLine.getOptionValue(CommandArgsEnum.LOAD.longname()),false);    
      Debug.log(3, "Sikuli-Script: requested to run: " + loadScript);
      givenScriptName = loadScript;
    }

    if (cmdLine.hasOption(CommandArgsEnum.RUN.shortname())) {
      givenScriptName = cmdLine.getOptionValue(CommandArgsEnum.RUN.longname());
    } else if (cmdLine.hasOption(CommandArgsEnum.TEST.shortname())) {
      givenScriptName = cmdLine.getOptionValue(CommandArgsEnum.TEST.longname());
      Debug.error("Command line option -t: not yet supported! %s", Arrays.asList(args).toString());
      runAsTest = true;
    }
    
    if (givenScriptName != null) {
      if (givenScriptName.endsWith(".skl")) {
        givenScriptName = FileManager.unzipSKL(givenScriptName);
        if (givenScriptName == null) {
          Debug.error(me + "not possible to make .skl runnable!");
          System.exit(1);
        }
      }
      Debug.log(3, me + "givenScriptName: " + givenScriptName);
      File sf = new File(givenScriptName);
      File script = FileManager.getScriptFile(sf, runner, args);
      if (script == null) {
        System.exit(1);
      }
      runner = SikuliX.getRunner();
      if (imagePath == null) {
        imagePath = FileManager.resolveImagePath(script);
      }
      SikuliX.callImagePathSetBundlePath(imagePath.getAbsolutePath());
      Debug.log(3, "Trying to run script: " + script.getAbsolutePath());
      int exitCode = runAsTest
              ? runner.runTest(script, imagePath, cmdArgs.getUserArgs(), null)
              : runner.runScript(script, imagePath, cmdArgs.getUserArgs(), null);
      runner.close();
      SikuliX.endNormal(exitCode);
    } else {
      Debug.error("Nothing to do according to the given commandline options!");
      cmdArgs.printHelp();
      if (runner != null) {
        System.out.println(runner.getCommandLineHelp());
      }
      System.exit(1);
    }
  }

  @Deprecated
  public static void popup(String message) {
    SikuliX.popup(message);
  }

  @Deprecated
  public static void popup(String message, String title) {
    SikuliX.popup( message,
            title);
  }

  @Deprecated
  public static String input(String msg) {
    return SikuliX.input(msg);
  }

  @Deprecated
  public static String input(String msg, boolean hidden) {
    return SikuliX.input(msg, hidden);
  }

  @Deprecated
  public static String input(String msg, String preset, boolean hidden) {
    return SikuliX.input(msg, preset, hidden);
  }

  @Deprecated
  public static String input(String msg, String preset, String title, boolean hidden) {
    return SikuliX.input(msg, preset, title, hidden);
  }
  
  @Deprecated
  public static String inputText(String msg, String preset, String title, int width, int lines) {
    return SikuliX.inputText(msg, preset, title, width, lines);
  }

  @Deprecated
  public static String run(String cmdline) {
    return SikuliX.run(cmdline);    
  }

  /**
   * Prints the interactive help from the ScriptRunner.
   */
  public static void shelp() {
    System.out.println(runner.getInteractiveHelp());
  }

  public static void cleanUp() {
    SikuliX.cleanUp(0);
  }
}
