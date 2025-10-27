package com.felipestanzani.beyondsight.model.base;

import lombok.*;
import org.springframework.data.neo4j.core.schema.Id;

@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
public class Element {
    @Id
    protected String id;

    @NonNull
    protected String name;
}
