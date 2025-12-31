package spl.lae;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;

import parser.*;
import scheduling.TiredExecutor;

public class Main {
  private static String outputPath;

  public static void main(String[] args) throws IOException {
    // TODO: main
    if (args.length != 3) {
      System.err.println("You sended more then 3 parameters: <number of threads> <input file> <output file>");
      return;
    }

    int numThreads = Integer.parseInt(args[0]); // get the first parameter in arg and conver it from string to int.
    String inputPath = args[1];
    outputPath = args[2];
    LinearAlgebraEngine lae = null;
    try {
      InputParser parser = new InputParser();
      ComputationNode root = parser.parse(inputPath);

      lae = new LinearAlgebraEngine(numThreads);
      ComputationNode resultNode = lae.run(root);

      OutputWriter.write(resultNode.getMatrix(), outputPath);

      System.out.println(lae.getWorkerReport());

    } catch (Exception e) {

      try {
        OutputWriter.write(e.getMessage(), outputPath);

      } catch (IOException ioEx) {
        System.err.println("Could not write error to file: " + ioEx.getMessage());
      }
      System.err.println("ERROR DETECTED: " + e.getMessage());
      System.exit(1);

    }
  }

  public static String getOutputPath() {
    return outputPath;
  }
}
