package co.com.pragma.bootcamp.application.api.dto;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PageableResponse <T>{
    private List<T> data;
    private int currentPage;
    private int totalPages;
    private long totalItems;
}
