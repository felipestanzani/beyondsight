package com.felipestanzani.beyondsight.execution;

import com.felipestanzani.jtoon.JToon;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.execution.ToolCallResultConverter;
import org.springframework.lang.Nullable;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Base64;
import java.util.Map;

@Slf4j
public class ToonCallResultConverter implements ToolCallResultConverter {

    @Override
    public String convert(@Nullable Object result, @Nullable Type returnType) {
        if (returnType == Void.TYPE) {
            log.debug("The tool has no return type. Converting to conventional response.");
            return JToon.encode("Done");
        }
        if (result instanceof RenderedImage renderedImage) {
            final var buf = new ByteArrayOutputStream(1024 * 4);
            try {
                ImageIO.write(renderedImage, "PNG", buf);
            }
            catch (IOException e) {
                return "Failed to convert tool result to a base64 image: " + e.getMessage();
            }
            final var imgB64 = Base64.getEncoder().encodeToString(buf.toByteArray());
            return JToon.encode(Map.of("mimeType", "image/png", "data", imgB64));
        }
        else {
            log.debug("Converting tool result to JSON.");
            return JToon.encode(result);
        }
    }
}
