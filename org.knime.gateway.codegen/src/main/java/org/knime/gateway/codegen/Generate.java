package org.knime.gateway.codegen;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import io.swagger.codegen.DefaultGenerator;
import io.swagger.codegen.config.CodegenConfigurator;

/**
 * Run code generation. Collects files from the "src-gen" folder ending with
 * "-config.json" and uses them to configure the code generators.
 */
public class Generate {
	public static void main(String[] args) throws IOException {
		// attempt to read from config files
		Files.list(Paths.get("src-gen")).filter(p -> p.toString().endsWith("-config.json")).forEach(p -> {
			CodegenConfigurator configurator = CodegenConfigurator
					.fromFile(p.toString());
			new DefaultGenerator().opts(configurator.toClientOptInput()).generate();
		});

	}
}
