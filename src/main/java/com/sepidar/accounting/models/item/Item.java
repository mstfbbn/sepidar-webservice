package com.sepidar.accounting.models.item;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Item {

    @SerializedName("ItemID")
    private Integer itemId;

    @SerializedName("Code")
    private String code;

    @SerializedName("Barcode")
    private String barcode;

    @SerializedName("Title")
    private String title;

    @SerializedName("IsActive")
    private Boolean isActive;

    @SerializedName("IsSellable")
    private Boolean isSellable;

    @SerializedName("Type")
    private Integer type;

    @SerializedName("UnitID")
    private Integer unitId;

    @SerializedName("SecondaryUnitID")
    private Integer secondaryUnitId;

    @SerializedName("UnitsRatio")
    private Double unitsRatio;

    @SerializedName("Weight")
    private Double weight;

    @SerializedName("Volume")
    private Double volume;

    @SerializedName("IsTaxExempt")
    private Boolean isTaxExempt;

    @SerializedName("TaxRate")
    private Double taxRate;

    @SerializedName("DutyRate")
    private Double dutyRate;

    @SerializedName("SaleGroupRef")
    private Integer saleGroupRef;

    @SerializedName("Tracings")
    private List<Tracing> tracings;

    @SerializedName("TracingInventories")
    private List<TracingInventory> tracingInventories;

    @SerializedName("TotalInventory")
    private Double totalInventory;

    @SerializedName("PropertyValues")
    private List<PropertyValue> propertyValues;

    @SerializedName("Thumbnail")
    private String thumbnail;

    @SerializedName("BrokerSellable")
    private Boolean brokerSellable;
}
