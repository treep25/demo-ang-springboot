package com.demo.backend.tutorial.model;

import com.demo.backend.user.Role;
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
    private String tutorialStatus;
    private Role role;
    public boolean isNaturalOrderSortingType() {
        return sortingType.equals("ASC");
    }
}
