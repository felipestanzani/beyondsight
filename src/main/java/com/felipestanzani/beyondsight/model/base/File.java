package com.felipestanzani.beyondsight.model.base;

import com.felipestanzani.beyondsight.model.enums.Language;
import lombok.*;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@Node("File")
public class File {
    @Id
    @GeneratedValue
    private String id;

    @NonNull
    @Property("name")
    private String name;

    @NonNull
    @Property("filePath")
    private String filePath;

    @NonNull
    @Property("language")
    private Language language;
}
