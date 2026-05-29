package com.example.demoStep7_3.controller;

import com.example.demoStep7_3.domain.Customer;
import com.example.demoStep7_3.dto.CustomerViewDto;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final DataSource dataSource;

    public CustomerController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // POST: 고객 등록
    /*
    @PostMapping
    public String addCustomer(@RequestBody CustomerAddDto dto) {
        int newId = customers.size() + 1; // 임시 ID 생성
        Customer c = new Customer(newId, dto.getName(), dto.getAddress(), dto.getPhone());
        customers.add(c);
        return "고객 등록 완료";
    }*/

    // GET: 전체 고객 조회
    @GetMapping("/all")
    public List<Customer> getCustomersFromMySQL() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT custid, name, address, phone FROM Customer";

        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery();
        ) {
            while (rs.next()) {
                Customer c = new Customer(
                        rs.getInt("custid"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getString("phone")
                );
                customers.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return customers;
    }

    @GetMapping
    public List<CustomerViewDto> getCustomersWithViewDto() {
            List<CustomerViewDto> result = new ArrayList<>();
            String sql = "SELECT custid, name, address FROM Customer";

            try (
                    Connection conn = dataSource.getConnection();
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    ResultSet rs = stmt.executeQuery()
            ) {
                while (rs.next()) {
                    result.add(new CustomerViewDto(
                            rs.getInt("custid"),
                            rs.getString("name"),
                            rs.getString("address")
                    ));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return result;
    }

    // GET: ID로 특정 고객 조회 (예: /api/customers/1)
    @GetMapping("/{custid}")
    public Customer getCustomerById(@PathVariable("custid") int custid) {
        System.out.println(">>> 요청된 custid 값: " + custid); // 서버 콘솔에 찍힘
        String sql = "SELECT custid, name, address, phone FROM Customer WHERE custid = ?";
        Customer customer = null;

        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, custid); // 첫 번째 ?에 id 값을 바인딩
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    customer = new Customer(
                            rs.getInt("custid"),
                            rs.getString("name"),
                            rs.getString("address"),
                            rs.getString("phone")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return customer; // 데이터가 없으면 null 반환
    }
}
