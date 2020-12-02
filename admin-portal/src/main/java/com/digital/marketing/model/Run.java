package com.digital.marketing.model;

import com.digital.marketing.entity.Campaign;
import com.digital.marketing.entity.Segment;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class Run {
    private Segment segment;
    private Campaign campaign;
}
