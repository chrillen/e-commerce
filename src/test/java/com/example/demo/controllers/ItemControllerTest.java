package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class ItemControllerTest {
    private ItemController itemController;
    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setUp() {
        this.itemController = new ItemController();
        TestUtils.injectObject(itemController,"itemRepository",itemRepository);
    }

    @Test
    public void getItemsTest() {
        ResponseEntity<List<Item>> response = itemController.getItems();
        List<Item> itemList =response.getBody();
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(itemList);
    }

    @Test
    public void getItemByIdTest() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(createItem()));
        ResponseEntity<Item> response = itemController.getItemById(1L);
        assertNotNull(response);
        assertEquals(200,response.getStatusCodeValue());
        Item item= response.getBody();
        assertNotNull(item);
    }

    @Test
    public void getItemByNameTest() {
        List<Item> items = new ArrayList<>();
        items.add(createItem());
        when(itemRepository.findByName("Snickers")).thenReturn(items);
        ResponseEntity<List<Item>> response = itemController.getItemsByName("Snickers");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(items, response.getBody());
    }

    //region helper functions
    public static Item createItem() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Snickers");
        item.setDescription("Snickers Snackbar");
        item.setPrice(BigDecimal.valueOf(1.5));
        return item;
    }
    //endregion

}
