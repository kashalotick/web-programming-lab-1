package org.example.hotel.io;

import org.example.hotel.model.Visitor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class VisitorCsvGateway {
    public void exportVisitors(Path filePath, List<Visitor> visitors, Comparator<Visitor> comparator) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            exportVisitors(writer, visitors, comparator);
        }
    }

    public void exportVisitors(Writer writer, List<Visitor> visitors, Comparator<Visitor> comparator) throws IOException {
        List<Visitor> copy = new ArrayList<>(visitors);
        copy.sort(comparator);

        writer.write("id,fullName,email\n");
        for (Visitor visitor : copy) {
            writer.write(visitor.getId() + "," + visitor.getFullName() + "," + visitor.getEmail() + "\n");
        }
        writer.flush();
    }

    public List<Visitor> importVisitors(Path filePath) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            return importVisitors(reader);
        }
    }

    public List<Visitor> importVisitors(Reader reader) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(reader);
        List<Visitor> result = new ArrayList<>();
        String line;
        boolean isHeader = true;

        while ((line = bufferedReader.readLine()) != null) {
            if (isHeader) {
                isHeader = false;
                continue;
            }
            String[] parts = line.split(",");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Некоректний CSV рядок: " + line);
            }
            result.add(new Visitor(parts[0], parts[1], parts[2]));
        }
        return result;
    }
}
