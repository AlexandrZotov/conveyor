package com.example.conveyor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JsonUtil {
    public static String readResourseAsString(String file) throws IOException {
        return new String(Files.readAllBytes(Paths.get(file)));
    }
}
