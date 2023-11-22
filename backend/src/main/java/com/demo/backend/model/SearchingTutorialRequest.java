package com.demo.backend.model;

import lombok.*;

@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class SearchingTutorialRequest {
    private String title;
    private String description;
    private String sortingType;

    public boolean isNaturalOrderSortingType() {
        return sortingType.equals("ASC");
    }
}
