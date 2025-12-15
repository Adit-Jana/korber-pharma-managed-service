package com.korberpharma.inventory_service.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BatchDetails implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private Integer batchId;
    private Integer quantity;
    @JsonFormat(pattern = "yyyy-mm-dd")
    private String expiryDate;
}
