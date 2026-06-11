package pl.epsi.jtts.parser.ts;

import pl.epsi.jtts.parser.ast.DeclarationNode;
import pl.epsi.jtts.parser.ast.method.Modifier;
import pl.epsi.jtts.parser.ir.JavaASTBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static pl.epsi.jtts.JTTSConfig.*;

public class TSCodeGen {

    public static void writeFile(Class<?> clazz) throws IOException {
        writeFile(clazz, clazz.getName().replace('.', '/'));
    }

    public static void writeFile(DeclarationNode cn, String classPath) throws IOException {
        TSFile c = new TSFile(cn);
        String path = JTTS_TYPINGS_PATH + classPath + ".d.ts";

        Files.createDirectories(Paths.get(path).getParent());

        try (FileWriter fw = new FileWriter(path)) {
            fw.write(c.write());
        } catch (Exception ignored) {}
    }

    public static void writeFile(Class<?> clazz, String classPath) throws IOException {
        DeclarationNode cn = JavaASTBuilder.compile(clazz);
        writeFile(cn, classPath);
    }

    public static void generatePackageJson() {
        String pckg = "{\n" +
                "  \"name\": \"@pts\",\n" +
                "  \"type\": \"module\",\n" +
                "  \"exports\": {\n" +
                "    \"./*\": \"./run/probets/ts/*\"\n" +
                "  }\n" +
                "}";

        try (FileWriter fw = new FileWriter(JTTS_TYPINGS_PATH + "package.json")) {
            fw.write(pckg);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create package json!", e);
        }
    }

    public static void generateTsConfig() {
        String conf = "{\n" +
                "  \"compilerOptions\": {\n" +
                "    \"baseUrl\": \".\",\n" +
                "    \"paths\": {\n" +
                "      \"@pts/*\": [\"run/probets/ts/*\"]\n" +
                "    }\n" +
                "  }\n" +
                "}";

        try (FileWriter fw = new FileWriter(JTTS_TYPINGS_PATH + "tsconfig.base.json")) {
            fw.write(conf);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create ts config!", e);
        }
    }

    public static void generateIndexFiles(Path dir) throws IOException {
        List<Path> files = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path path : stream) {
                if (Files.isRegularFile(path)) {
                    files.add(path);
                } else if (Files.isDirectory(path)) {
                    generateIndexFiles(path);
                }
            }
        }

        if (files.isEmpty()) return;
        try (FileWriter fw = new FileWriter(dir + "/index.d.ts")) {
            for (Path path : files) {
                path = path.toAbsolutePath().normalize();

                if (path.endsWith("index.d.ts")) continue;

                Path relative = Paths.get(JTTS_TYPINGS_PATH).toAbsolutePath().normalize().relativize(path);

                String p = relative.toString();

                int dot = p.lastIndexOf(".d.ts");
                if (dot != -1) {
                    p = p.substring(0, dot);
                }

                fw.write("export * from \"");
                fw.write("@pts/");
                fw.write(p);
                fw.write("\"\n");
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to create index.d.ts for directory: " + dir, e);
        }
    }

    public static void main(String[] args) throws IOException {
        //ignoreParameters.add(Object.class);
        //generateFile(HashMap.class);
        writeFile(JavaASTBuilder.class);
        writeFile(Modifier.class);

        generateIndexFiles(Path.of(JTTS_TYPINGS_PATH));
        generatePackageJson();
        generateTsConfig();
    }



}
