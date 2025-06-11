package org.scoula.ex03.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class SampleDTOList {
	private List<SampleDTO> list;

	public SampleDTOList(){
		list = new ArrayList<>();
	}

}
