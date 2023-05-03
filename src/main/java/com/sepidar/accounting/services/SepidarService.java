package com.sepidar.accounting.services;

import com.sepidar.accounting.models.administrative_divisions.AdministrativeDivisionDTO;
import com.sepidar.accounting.models.authentication.DeviceRegisterResponseDTO;
import com.sepidar.accounting.models.authentication.LoginResponse;
import com.sepidar.accounting.models.bank.Bank;
import com.sepidar.accounting.models.bank.BankAccount;
import com.sepidar.accounting.models.bank.ReceiptNew;
import com.sepidar.accounting.models.bank.ReceiptResult;
import com.sepidar.accounting.models.common.SepidarConfiguration;
import com.sepidar.accounting.models.currency.Currency;
import com.sepidar.accounting.models.customer.Customer;
import com.sepidar.accounting.models.customer.CustomerAdd;
import com.sepidar.accounting.models.customer.CustomerEdit;
import com.sepidar.accounting.models.customer.CustomerGrouping;
import com.sepidar.accounting.models.general.GenerationVersion;
import com.sepidar.accounting.models.invoice.Invoice;
import com.sepidar.accounting.models.invoice.InvoiceBatch;
import com.sepidar.accounting.models.invoice.InvoiceBatchResult;
import com.sepidar.accounting.models.item.Inventory;
import com.sepidar.accounting.models.item.Item;
import com.sepidar.accounting.models.price_note.PriceNoteItem;
import com.sepidar.accounting.models.property.Property;
import com.sepidar.accounting.models.quotation.BatchResult;
import com.sepidar.accounting.models.quotation.Quotation;
import com.sepidar.accounting.models.quotation.QuotationBatch;
import com.sepidar.accounting.models.quotation.QuotationBatchResult;
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

    List<PriceNoteItem> getPriceNoteItems(String xmlString, String token);

    List<Currency> getCurrencies(String xmlString, String token);

    List<Quotation> getQuotations(String xmlString, String token, String fromDate, String toDate);

    Quotation getQuotation(String xmlString, String token, Integer quotationId);

    Quotation createQuotation(String xmlString, String token, QuotationBatch quotation);

    List<QuotationBatchResult> createQuotationBatch(String xmlString, String token, List<QuotationBatch> quotationBatchList);

    void closeQuotation(String xmlString, String token, Integer quotationId);

    List<BatchResult> closeQuotationBatch(String xmlString, String token, List<Integer> quotationIdList);

    void uncloseQuotation(String xmlString, String token, Integer quotationId);

    List<BatchResult> uncloseQuotationBatch(String xmlString, String token, List<Integer> quotationIdList);

    void deleteQuotation(String xmlString, String token, Integer quotationId);

    List<BatchResult> deleteQuotationBatch(String xmlString, String token, List<Integer> quotationIdList);

    List<Invoice> getInvoices(String xmlString, String token);

    Invoice getInvoice(String xmlString, String token, Integer invoiceId);

    Invoice createInvoice(String xmlString, String token, InvoiceBatch invoice);

    List<InvoiceBatchResult> createInvoiceBatch(String xmlString, String token, List<InvoiceBatch> invoiceBatchList);

    Invoice createInvoiceBasedOnQuotation(String xmlString, String token, Integer quotationId);

    List<Bank> getBanks(String xmlString, String token);

    List<BankAccount> getBankAccounts(String xmlString, String token);

    ReceiptResult saveReceiptBasedOnInvoice(String xmlString, String token, ReceiptNew receipt);
}
