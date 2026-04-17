package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.entities.DiningCommonsMenuItem;
import edu.ucsb.cs156.example.repositories.DiningCommonsMenuItemRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ucsbdiningcommonsmenuitem")
public class UCSBDiningCommonsMenuItemController {

  private final DiningCommonsMenuItemRepository diningCommonsMenuItemRepository;

  public UCSBDiningCommonsMenuItemController(
      DiningCommonsMenuItemRepository diningCommonsMenuItemRepository) {
    this.diningCommonsMenuItemRepository = diningCommonsMenuItemRepository;
  }

  @PostMapping("/post")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public DiningCommonsMenuItem postMenuItem(
      @RequestParam String diningCommonsCode,
      @RequestParam String name,
      @RequestParam String station) {
    DiningCommonsMenuItem item =
        DiningCommonsMenuItem.builder()
            .station(station)
            .name(name)
            .diningCommonsCode(diningCommonsCode)
            .build();

    return diningCommonsMenuItemRepository.save(item);
  }

  @GetMapping("/all")
  @PreAuthorize("hasRole('ROLE_USER')")
  public Iterable<DiningCommonsMenuItem> allMenuItems() {
    return diningCommonsMenuItemRepository.findAll();
  }
}
