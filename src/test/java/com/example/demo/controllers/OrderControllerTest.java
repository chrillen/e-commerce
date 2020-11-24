package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import javax.persistence.criteria.Order;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {
    private OrderController orderController;
    private OrderRepository orderRepository = mock(OrderRepository.class);
    private UserRepository userRepository = mock(UserRepository.class);


    @Before
    public void setUp() {
        this.orderController = new OrderController();
        TestUtils.injectObject(orderController,"orderRepository",orderRepository);
        TestUtils.injectObject(orderController,"userRepository",userRepository);
    }

    @Test
    public void createOrderTest() {
        User u = UserControllerTest.createUser();
        u.setCart(CartControllerTest.generateCart(u));
        when(userRepository.findByUsername(u.getUsername())).thenReturn(u);
        final ResponseEntity<UserOrder> response = orderController.submit(u.getUsername());
        assertNotNull(response);
        assertEquals(200,response.getStatusCodeValue());
        UserOrder userOrder = response.getBody();
        assertNotNull(userOrder);
        assertEquals("test",userOrder.getUser().getUsername());
    }

    @Test
    public void getOrdersByNameTest() {
        List<UserOrder> r = new ArrayList<>();
        r.add(createOrder());
        User u = UserControllerTest.createUser();
        when(userRepository.findByUsername(u.getUsername())).thenReturn(u);
        when(orderRepository.findByUser(u)).thenReturn(r);

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(u.getUsername());
        List<UserOrder> orderList = response.getBody();
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(orderList);
    }


    //region helper functions
    public static UserOrder createOrder(){
        UserOrder userOrder = new UserOrder();
        userOrder.setId(1L);
        List<Item> items = new ArrayList<Item>();
        items.add(ItemControllerTest.createItem());
        userOrder.setItems(items);
        BigDecimal total = items.stream().map(s -> s.getPrice()).reduce(BigDecimal.ZERO,BigDecimal::add);
        userOrder.setTotal(total);
        userOrder.setUser(UserControllerTest.createUser());
        return userOrder;
    }
    //endregion
}
