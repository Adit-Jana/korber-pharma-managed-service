package com.korberpharma.inventory_service.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Builder
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class InventoryResDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private Long productId;
    private String productName;
    private List<BatchDetails> batches;
}
