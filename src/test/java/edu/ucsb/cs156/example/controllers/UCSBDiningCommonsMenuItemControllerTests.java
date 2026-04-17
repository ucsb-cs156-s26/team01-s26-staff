package edu.ucsb.cs156.example.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import edu.ucsb.cs156.example.ControllerTestCase;
import edu.ucsb.cs156.example.entities.DiningCommonsMenuItem;
import edu.ucsb.cs156.example.repositories.DiningCommonsMenuItemRepository;
import edu.ucsb.cs156.example.repositories.UserRepository;
import edu.ucsb.cs156.example.testconfig.TestConfig;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MvcResult;

@WebMvcTest(UCSBDiningCommonsMenuItemController.class)
@Import(TestConfig.class)
public class UCSBDiningCommonsMenuItemControllerTests extends ControllerTestCase {

  @MockitoBean private DiningCommonsMenuItemRepository diningCommonsMenuItemRepository;

  @MockitoBean UserRepository userRepository;

  @WithMockUser(roles = {"USER"})
  @Test
  public void logged_in_user_can_get_all_menu_items() throws Exception {

    DiningCommonsMenuItem item1 =
        DiningCommonsMenuItem.builder()
            .name("item 1")
            .diningCommonsCode("station 1")
            .diningCommonsCode("de-la-guerra")
            .build();

    DiningCommonsMenuItem item2 =
        DiningCommonsMenuItem.builder()
            .name("item 2")
            .diningCommonsCode("station 2")
            .diningCommonsCode("carrillo")
            .build();

    ArrayList<DiningCommonsMenuItem> expectedItems = new ArrayList<>(Arrays.asList(item1, item2));

    when(diningCommonsMenuItemRepository.findAll()).thenReturn(expectedItems);

    MvcResult response =
        mockMvc
            .perform(get("/api/ucsbdiningcommonsmenuitem/all"))
            .andExpect(status().isOk())
            .andReturn();

    verify(diningCommonsMenuItemRepository, times(1)).findAll();
    List<DiningCommonsMenuItem> retrievedItems =
        mapper.readValue(response.getResponse().getContentAsString(), new TypeReference<>() {});

    assertTrue(retrievedItems.containsAll(expectedItems));
  }

  @WithMockUser(roles = {"ADMIN", "USER"})
  @Test
  public void admin_can_post_new_menu_item() throws Exception {
    // arrange

    DiningCommonsMenuItem item =
        DiningCommonsMenuItem.builder()
            .name("item 1")
            .station("station 1")
            .diningCommonsCode("de-la-guerra")
            .build();

    when(diningCommonsMenuItemRepository.save(eq(item))).thenReturn(item);

    // act
    MvcResult response =
        mockMvc
            .perform(
                post("/api/ucsbdiningcommonsmenuitem/post")
                    .with(csrf())
                    .param("name", "item 1")
                    .param("station", "station 1")
                    .param("diningCommonsCode", "de-la-guerra"))
            .andExpect(status().isOk())
            .andReturn();

    // assert
    verify(diningCommonsMenuItemRepository, times(1)).save(item);
    String expectedJson = mapper.writeValueAsString(item);
    String responseString = response.getResponse().getContentAsString();
    assertEquals(expectedJson, responseString);
  }

  @Test
  public void logged_out_users_cannot_get_all() throws Exception {
    mockMvc
        .perform(get("/api/ucsbdiningcommonsmenuitem/all"))
        .andExpect(status().is(403)); // logged out users can't get all
  }

  @WithMockUser(roles = {"USER"})
  @Test
  public void logged_in_users_can_get_all() throws Exception {
    mockMvc
        .perform(get("/api/ucsbdiningcommonsmenuitem/all"))
        .andExpect(status().is(200)); // logged
  }

  // Authorization tests for /api/ucsbdiningcommonsmenuitem/post
  // (Perhaps should also have these for put and delete)

  @Test
  public void logged_out_users_cannot_post() throws Exception {
    mockMvc.perform(post("/api/ucsbdiningcommonsmenuitem/post")).andExpect(status().is(403));
  }

  @WithMockUser(roles = {"USER"})
  @Test
  public void logged_in_regular_users_cannot_post() throws Exception {
    mockMvc
        .perform(post("/api/ucsbdiningcommonsmenuitem/post"))
        .andExpect(status().is(403)); // only admins can post
  }

  @Test
  public void logged_out_users_cannot_get_by_id() throws Exception {
    mockMvc
        .perform(get("/api/ucsbdiningcommonsmenuitem?id=7"))
        .andExpect(status().is(403)); // logged out users can't get by id
  }

  @WithMockUser(roles = {"USER"})
  @Test
  public void test_that_logged_in_user_can_get_by_id_when_the_id_exists() throws Exception {

    DiningCommonsMenuItem item =
        DiningCommonsMenuItem.builder()
            .id(7L)
            .name("item 1")
            .station("station 1")
            .diningCommonsCode("de-la-guerra")
            .build();

    when(diningCommonsMenuItemRepository.findById(eq(7L))).thenReturn(Optional.of(item));

    // act
    MvcResult response =
        mockMvc
            .perform(get("/api/ucsbdiningcommonsmenuitem?id=7"))
            .andExpect(status().isOk())
            .andReturn();

    // assert

    verify(diningCommonsMenuItemRepository, times(1)).findById(eq(7L));
    DiningCommonsMenuItem recievedItem =
        mapper.readValue(response.getResponse().getContentAsString(), DiningCommonsMenuItem.class);
    assertEquals(item, recievedItem);
  }

  @WithMockUser(roles = {"USER"})
  @Test
  public void test_that_logged_in_user_can_get_by_id_when_the_id_does_not_exist() throws Exception {

    // arrange

    when(diningCommonsMenuItemRepository.findById(eq(7L))).thenReturn(Optional.empty());

    // act
    MvcResult response =
        mockMvc
            .perform(get("/api/ucsbdiningcommonsmenuitem?id=7"))
            .andExpect(status().isNotFound())
            .andReturn();

    // assert

    verify(diningCommonsMenuItemRepository, times(1)).findById(eq(7L));
    Map<String, Object> json = responseToJson(response);
    assertEquals("EntityNotFoundException", json.get("type"));
    assertEquals("DiningCommonsMenuItem with id 7 not found", json.get("message"));
  }

  @WithMockUser(roles = {"ADMIN", "USER"})
  @Test
  public void admin_can_edit_an_existing_menuitem() throws Exception {
    DiningCommonsMenuItem original =
        DiningCommonsMenuItem.builder()
            .id(67L)
            .station("station 1")
            .diningCommonsCode("de-la-guerra")
            .name("item 1")
            .build();

    DiningCommonsMenuItem edited =
        DiningCommonsMenuItem.builder()
            .id(67L)
            .station("an entirely new, cooler station")
            .diningCommonsCode("carillo")
            .name("still item 1!")
            .build();

    // arrange

    when(diningCommonsMenuItemRepository.findById(eq(67L))).thenReturn(Optional.of(original));

    // act
    MvcResult response =
        mockMvc
            .perform(
                put("/api/ucsbdiningcommonsmenuitem")
                    .param("id", "67")
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(mapper.writeValueAsString(edited))
                    .with(csrf()))
            .andExpect(status().isOk())
            .andReturn();

    // assert
    verify(diningCommonsMenuItemRepository, times(1)).findById(67L);
    verify(diningCommonsMenuItemRepository, times(1))
        .save(edited); // should be saved with correct user
    DiningCommonsMenuItem savedItem =
        mapper.readValue(response.getResponse().getContentAsString(), DiningCommonsMenuItem.class);
    assertEquals(edited, savedItem);
  }

  @WithMockUser(roles = {"ADMIN", "USER"})
  @Test
  public void admin_cannot_edit_menu_item_that_does_not_exist() throws Exception {
    // arrange

    DiningCommonsMenuItem edited =
        DiningCommonsMenuItem.builder()
            .id(67L)
            .station("an entirely new, cooler station")
            .diningCommonsCode("carillo")
            .name("still item 1!")
            .build();

    when(diningCommonsMenuItemRepository.findById(eq(67L))).thenReturn(Optional.empty());

    // act
    MvcResult response =
        mockMvc
            .perform(
                put("/api/ucsbdiningcommonsmenuitem")
                    .param("id", "67")
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(mapper.writeValueAsString(edited))
                    .with(csrf()))
            .andExpect(status().isNotFound())
            .andReturn();

    // assert
    verify(diningCommonsMenuItemRepository, times(1)).findById(67L);
    Map<String, Object> json = responseToJson(response);
    assertEquals("DiningCommonsMenuItem with id 67 not found", json.get("message"));
  }
}
