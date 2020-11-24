package com.example.demo.controllers;

import com.example.demo.SareetaApplication;
import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static com.example.demo.TestUtils.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CartControllerTest {

    private CartController cartController;
    private UserRepository userRepository = mock(UserRepository.class);
    private ItemRepository itemRepository = mock(ItemRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);


    @Before
    public void setUp() {
        this.cartController = new CartController();
        TestUtils.injectObject(cartController,"userRepository", userRepository);
        TestUtils.injectObject(cartController,"itemRepository", itemRepository);
        TestUtils.injectObject(cartController,"cartRepository", cartRepository);
    }

    @Test
    public void addToCartNoUserError() {
        ModifyCartRequest modifyCartRequest = createModifyCartRequest("", 1, 1);
        ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void addToCartNoItemError() {
        when(userRepository.findByUsername("test")).thenReturn(new User());
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());
        ModifyCartRequest modifyCartRequest = createModifyCartRequest("test", 1, 1);
        ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);
        assertNotNull(response);
        verify(itemRepository, times(1)).findById(1L);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void addToCartTest() {
        User user =  UserControllerTest.createUser();
        Item item =  ItemControllerTest.createItem();
        Cart cart = user.getCart();
        cart.addItem(item);
        cart.setUser(user);
        user.setCart(cart);
        when(userRepository.findByUsername("test")).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        ModifyCartRequest modifyCartRequest = createModifyCartRequest("test", 1, 1);
        ResponseEntity<Cart> responseEntity = cartController.addTocart(modifyCartRequest);
        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());
        Cart responseCart = responseEntity.getBody();
        assertNotNull(responseCart);
        List<Item> items = responseCart.getItems();
        assertNotNull(items);
        assertEquals("test", responseCart.getUser().getUsername());
        verify(cartRepository, times(1)).save(responseCart);
    }

    @Test
    public void removeFromCartNoUserError() {
        ModifyCartRequest modifyCartRequest = createModifyCartRequest("", 1, 1);
        ResponseEntity<Cart> responseEntity = cartController.removeFromcart(modifyCartRequest);
        assertNotNull(responseEntity);
        assertEquals(404, responseEntity.getStatusCodeValue());
    }

    @Test
    public void removeFromCartNoItemError() {
        when(userRepository.findByUsername("test")).thenReturn(new User());
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());
        ModifyCartRequest modifyCartRequest = createModifyCartRequest("test", 1, 1);
        ResponseEntity<Cart> responseEntity = cartController.removeFromcart(modifyCartRequest);
        assertNotNull(responseEntity);
        verify(itemRepository, times(1)).findById(1L);
        assertEquals(404, responseEntity.getStatusCodeValue());
    }

    @Test
    public void removeFromCartTest() {
        User user = UserControllerTest.createUser();
        Item item = ItemControllerTest.createItem();
        Cart cart = user.getCart();
        cart.addItem(item);
        cart.setUser(user);
        user.setCart(cart);
        when(userRepository.findByUsername("test")).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        ModifyCartRequest modifyCartRequest = createModifyCartRequest("test", 1, 1);
        ResponseEntity<Cart> responseEntity = cartController.removeFromcart(modifyCartRequest);
        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());
        Cart responseCart = responseEntity.getBody();
        assertNotNull(responseCart);
        List<Item> items = responseCart.getItems();
        assertNotNull(items);
        assertEquals(0, items.size());
        assertEquals("test", responseCart.getUser().getUsername());
        verify(cartRepository, times(1)).save(responseCart);
    }

    //region helper functions for tests
    public static Cart generateCart(User u){
        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUser(u);
        List<Item> items = new ArrayList<Item>();
        Item item = ItemControllerTest.createItem();
        items.add(item);
        cart.setItems(items);
        cart.setTotal(item.getPrice());
        return cart;
    }
    public static Cart emptyCart(){
        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUser(null);
        cart.setItems(new ArrayList<Item>());
        cart.setTotal(BigDecimal.valueOf(0.0));
        return cart;
    }
    public static ModifyCartRequest createModifyCartRequest(String username, long itemId, int quantity) {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername(username);
        modifyCartRequest.setItemId(itemId);
        modifyCartRequest.setQuantity(quantity);
        return modifyCartRequest;
    }
    //endregion

}
