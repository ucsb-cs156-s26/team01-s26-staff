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

  /**
   * Post a new Menu Item
   *
   * @param diningCommonsCode the code of the dining commons
   * @param name the name of the menu item
   * @param station the station where the menu item is located
   * @return the created {@link DiningCommonsMenuItem}
   */
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

  /**
   * Get all Menu Items
   *
   * @return a list of all {@link DiningCommonsMenuItem}
   */
  @GetMapping("/all")
  @PreAuthorize("hasRole('ROLE_USER')")
  public Iterable<DiningCommonsMenuItem> allMenuItems() {
    return diningCommonsMenuItemRepository.findAll();
  }
}
