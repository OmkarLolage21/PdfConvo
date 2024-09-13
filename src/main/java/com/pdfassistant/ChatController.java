package com.programming.techie.pdfassistant;

import dev.langchain4j.chain.ConversationalRetrievalChain;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;

import static com.programming.techie.pdfassistant.PdfAssistantApplication.toPath;
import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.loadDocument;


@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatController {
    @Autowired
    EmbeddingStoreIngestor embeddingStoreIngestor;

    @Autowired
    ConversationalRetrievalChain conversationalRetrievalChain;

    @PostMapping
    public String chatWithPdf(@RequestBody String text) {
        Document document = loadDocument(Paths.get("D:\\abc.pdf"), new ApachePdfBoxDocumentParser());
        embeddingStoreIngestor.ingest(document);
        var answer = conversationalRetrievalChain.execute(text);
        log.debug("Answer is - {}", answer);
        return answer;
    }

    @PostMapping("/uploadPdf")
    public String handleFileUpload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return "Please select a file to upload.";
        }
        try {
            String fileName = file.getOriginalFilename();
            File destFile = new File("D:\\abc.pdf");
            file.transferTo(destFile);
            System.out.println("File uploaded successfully: ");
            return "File uploaded successfully: " + fileName;
        } catch (IOException e) {
            System.out.println(e);
            return "Failed to upload file: " + e.getMessage();
        }
    }
}
