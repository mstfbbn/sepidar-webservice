package com.sepidar.accounting.services;

import com.sepidar.accounting.models.administrative_divisions.AdministrativeDivisionDTO;
import com.sepidar.accounting.models.authentication.DeviceRegisterResponseDTO;
import com.sepidar.accounting.models.authentication.LoginResponse;
import com.sepidar.accounting.models.common.SepidarConfiguration;
import com.sepidar.accounting.models.customer.Customer;
import com.sepidar.accounting.models.customer.CustomerAdd;
import com.sepidar.accounting.models.customer.CustomerEdit;
import com.sepidar.accounting.models.customer.CustomerGrouping;
import com.sepidar.accounting.models.general.GenerationVersion;
import com.sepidar.accounting.models.invoice.NewInvoiceRequest;
import com.sepidar.accounting.models.item.Inventory;
import com.sepidar.accounting.models.item.Item;
import com.sepidar.accounting.models.property.Property;
import com.sepidar.accounting.models.stock.Stock;
import com.sepidar.accounting.models.unit.Unit;
import com.sepidar.accounting.services.impl.SepidarServiceImpl;

import java.util.List;

public interface SepidarService {

    static SepidarService getInstance(SepidarConfiguration configuration) {
        return new SepidarServiceImpl(configuration);
    }

    DeviceRegisterResponseDTO register();

    LoginResponse login(String rsaPublicKeyXmlString, String username, String password);

    boolean isAuthenticated(String xmlString, String token);

    GenerationVersion generationVersion();

    List<AdministrativeDivisionDTO> administrativeDivision(String xmlString, String token);

    List<CustomerGrouping> customerGroupings(String xmlString, String token);

    List<Customer> customers(String xmlString, String token);

    Customer customer(String xmlString, String token, Integer customerId);

    Customer customerAdd(String xmlString, String token, CustomerAdd customerAdd);

    Customer customerEdit(String xmlString, String token, CustomerEdit customerEdit);

    List<Unit> getUnits(String xmlString, String token);

    List<Property> getProperties(String xmlString, String token);

    List<Stock> getStocks(String xmlString, String token);

    List<Item> getItems(String xmlString, String token);

    String getItemImage(String xmlString, String token, Integer itemId);

    List<Inventory> getInventories(String xmlString, String token);

    void createNewInvoice(String xmlString, String token, NewInvoiceRequest request);
}
