package com.snacks.backend.service;

import com.snacks.backend.dto.WidgetDto;
import com.snacks.backend.entity.Widget;
import com.snacks.backend.repository.WidgetRepository;
import java.util.List;
import java.util.Vector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WidgetService {

  @Autowired
  WidgetRepository widgetRepository;

 public WidgetService(WidgetRepository widgetRepository) {
   this.widgetRepository = widgetRepository;
 }
  public WidgetDto[] getWidgets() {
    Vector<WidgetDto> vector = new Vector<>();

    List<Widget> widgets = widgetRepository.findAll();

    for (Widget widget : widgets) {
      WidgetDto widgetDto = new WidgetDto();
      widgetDto.setName(widget.getName());
      widgetDto.setImage(widget.getImage());
      widgetDto.setDescription(widget.getDescription());

      vector.add(widgetDto);
    }

    WidgetDto[] widgetDtos = new WidgetDto[vector.size()];
    for (int i = 0; i< vector.size(); i++) {
      widgetDtos[i] = vector.get(i);
    }
    return widgetDtos;
  }
}
