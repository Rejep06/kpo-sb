package hse.antiplag.analysis.service;


import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Строит URL к внешнему API quickchart.io для облака слов.
 */
@Service
public class WordCloudService {

    public String buildWordCloudUrl(String text) {
        String configJson = """
            {
              "type": "wordcloud",
              "data": {
                "labels": [%s]
              }
            }
            """.formatted(toLabels(text));

        String encodedConfig;
        try {
            encodedConfig = URLEncoder.encode(configJson, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }

        return "https://quickchart.io/wordcloud?c=" + encodedConfig;
    }

    private String toLabels(String text) {
        // Разбиваем по пробелам и экранируем
        String[] words = text.split("\\s+");
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String w : words) {
            if (w.isBlank()) continue;
            if (!first) sb.append(",");
            sb.append("\"").append(w.replace("\"", "\\\"")).append("\"");
            first = false;
        }
        return sb.toString();
    }
}
