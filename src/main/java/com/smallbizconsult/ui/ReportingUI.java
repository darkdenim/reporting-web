package com.smallbizconsult.ui;

import com.smallbizconsult.model.ModelA;
import com.vaadin.annotations.Widgetset;
import com.vaadin.data.Item;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.*;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.dussan.vaadin.dcharts.DCharts;
import org.dussan.vaadin.dcharts.base.elements.Trendline;
import org.dussan.vaadin.dcharts.base.elements.XYaxis;
import org.dussan.vaadin.dcharts.base.elements.XYseries;
import org.dussan.vaadin.dcharts.base.renderers.MarkerRenderer;
import org.dussan.vaadin.dcharts.data.DataSeries;
import org.dussan.vaadin.dcharts.data.Ticks;
import org.dussan.vaadin.dcharts.metadata.TooltipAxes;
import org.dussan.vaadin.dcharts.metadata.XYaxes;
import org.dussan.vaadin.dcharts.metadata.Yaxes;
import org.dussan.vaadin.dcharts.metadata.locations.TooltipLocations;
import org.dussan.vaadin.dcharts.metadata.renderers.AxisRenderers;
import org.dussan.vaadin.dcharts.metadata.renderers.LabelRenderers;
import org.dussan.vaadin.dcharts.metadata.renderers.SeriesRenderers;
import org.dussan.vaadin.dcharts.metadata.styles.MarkerStyles;
import org.dussan.vaadin.dcharts.options.*;
import org.dussan.vaadin.dcharts.renderers.axis.LinearAxisRenderer;
import org.dussan.vaadin.dcharts.renderers.tick.AxisTickRenderer;
//import org.vaadin.spring.VaadinUI;
import org.vaadin.spring.annotation.VaadinUI;

import java.io.File;
import java.io.FileInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@VaadinUI
@Widgetset("com.smallbizconsult.ui.AppWidgetSet")
public class ReportingUI extends UI {

    public static final String fileLocation = "C:\\git\\Sample - Superstore Sales (Excel).xls";

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        Table table = new Table("Superstore Sales");
        table.addContainerProperty(ModelA.Headings.RowID.caption, Long.class, null);
        table.addContainerProperty(ModelA.Headings.OrderID.caption, Long.class, null);
        table.addContainerProperty(ModelA.Headings.OrderDate.caption, Date.class, null);
        table.addContainerProperty(ModelA.Headings.OrderQuantity.caption, Integer.class, null);
        table.addContainerProperty(ModelA.Headings.CustomerName.caption, String.class, null);
        table.addContainerProperty(ModelA.Headings.ProductName.caption, String.class, null);
        table.addContainerProperty(ModelA.Headings.Sales.caption, Double.class, null);
        table.addContainerProperty(ModelA.Headings.Region.caption, String.class, null);
        table.addContainerProperty(ModelA.Headings.Province.caption, String.class, null);
        table.addContainerProperty(ModelA.Headings.Profit.caption, Double.class, null);

        List<ModelA> models = buildModel();
        Map<String, Integer> map = new HashMap<>();
        List<Integer> qData = new ArrayList<>();
        Map<String, Integer> monthlyQtyMap = new TreeMap<>();
        Map<String, Integer> yearSalesMap = new TreeMap<>();
        for (ModelA model : models) {
            Object itemId = table.addItem();
            Item item = table.getItem(itemId);
            item.getItemProperty(ModelA.Headings.RowID.caption).setValue(model.getRowId());
            item.getItemProperty(ModelA.Headings.OrderID.caption).setValue(model.getOrderId());
            item.getItemProperty(ModelA.Headings.OrderDate.caption).setValue(model.getOrderDate());
            item.getItemProperty(ModelA.Headings.OrderQuantity.caption).setValue(model.getQuantity());
            item.getItemProperty(ModelA.Headings.CustomerName.caption).setValue(model.getCustomer());
            item.getItemProperty(ModelA.Headings.ProductName.caption).setValue(model.getProductName());
            item.getItemProperty(ModelA.Headings.Sales.caption).setValue(model.getSales());
            item.getItemProperty(ModelA.Headings.Region.caption).setValue(model.getRegion());
            item.getItemProperty(ModelA.Headings.Province.caption).setValue(model.getProvince());
            item.getItemProperty(ModelA.Headings.Profit.caption).setValue(model.getProfit());

            String key = model.getCustomer();
            int qty = model.getQuantity();
            if (map.containsKey(key)) {
                qty = map.get(key) + qty;
            }

            if (map.keySet().size() < 10) {
                map.put(key, qty);
            }

            String year = new SimpleDateFormat("yyyy").format(model.getOrderDate());
            if (yearSalesMap.containsKey(year)) {
                yearSalesMap.put(year, yearSalesMap.get(year) + model.getQuantity());
            } else {
                yearSalesMap.put(year, model.getQuantity());
            }

            // monthly quantity
            String month = new SimpleDateFormat("MMM-yyyy").format(model.getOrderDate());
            Integer mQty = 0;
            if (!monthlyQtyMap.containsKey(month)) {
                mQty = model.getQuantity();
            } else {
                mQty += monthlyQtyMap.get(month) + model.getQuantity();
            }

            monthlyQtyMap.put(month, mQty);
        }

//        VerticalLayout layout = new VerticalLayout();
//        layout.addComponent(table);
        String[] names = new String[map.keySet().size()];

        DCharts chart = (DCharts)generateCharts(map.values().toArray(new Integer[map.values().size()]), map.keySet().toArray(names));
        chart.setCaption("Bar Chart");
        DCharts lineChart = (DCharts)drawLineChart(monthlyQtyMap, names);
        DCharts salesChart = (DCharts)drawMonthlySalesChart(monthlyQtyMap, yearSalesMap);
        DCharts monthlySalesChart = (DCharts)monthlySalesChart(yearSalesMap);

//        layout.addComponent(chart);
//        layout.addComponent(lineChart);
//        layout.addComponent(salesChart);
//        layout.addComponent(monthlySalesChart);

        TabSheet chartTabSheet = new TabSheet();
        chartTabSheet.addTab(chart, "Bar", new ThemeResource("img/icon1.png"));
        chartTabSheet.addTab(lineChart, "Line", new ThemeResource("img/icon1.png"));
        chartTabSheet.addTab(salesChart, "Sales", new ThemeResource("img/icon1.png"));
        chartTabSheet.addTab(monthlySalesChart, "Monthly Sales", new ThemeResource("img/icon1.png"));

        TabSheet tabSheet = new TabSheet();
        tabSheet.addTab(table, "Data", new ThemeResource("img/report_icon.jpg"));
        tabSheet.addTab(chartTabSheet, "Charts", new ThemeResource("img/icon1.png"));

        setContent(tabSheet);
    }

    private Component generateCharts(Integer[] qData, String[] names) {
        DataSeries dataSeries = new DataSeries()
                .add(qData);

        SeriesDefaults seriesDefaults = new SeriesDefaults()
                .setRenderer(SeriesRenderers.BAR);

        Axes axes = new Axes()
                .addAxis(
                        new XYaxis()
                                .setRenderer(AxisRenderers.CATEGORY)
                                .setTicks(
                                        new Ticks()
                                                .add(names)));
//                                                .add("a", "b", "c", "d")));

        Highlighter highlighter = new Highlighter()
                .setShow(false);

        Options options = new Options()
                .setSeriesDefaults(seriesDefaults)
                .setAxes(axes)
                .setAnimate(true)
                .setTitle("Top 10 Customers")
                .setHighlighter(highlighter);
//                .setSeries(series);

        DCharts chart = new DCharts()
                .setDataSeries(dataSeries)
                .setOptions(options)
                .show();

        return chart;
    }

    private Component drawLineChart(Map<String, Integer> map, String[] names) {
        Title title = new Title("Plot With Options");

        AxesDefaults axesDefaults = new AxesDefaults()
                .setLabelRenderer(LabelRenderers.CANVAS);

        Axes axes = new Axes()
                .addAxis(
                        new XYaxis()
                                .setLabel("Time")
                                .setPad(0))
                .addAxis(
                        new XYaxis(XYaxes.Y)
                                .setLabel("Quantity"));

        List<XYseries> seriesList = new ArrayList<>();
        for (int i = 0; i < names.length; i++) {
            XYseries xYseries = new XYseries().setLabel(names[i]);
            seriesList.add(xYseries);
        }

        Series series = new Series();
        for(XYseries xYseries : seriesList) {
            series.addSeries(xYseries);
        }

        Cursor cursor = new Cursor()
                .setShow(true);

        Options options = new Options()
                .setAxesDefaults(axesDefaults)
                .setAxes(axes)
                .addOption(cursor)
                .setAnimate(true)
                .setTitle("Sales over time")
                .setSeries(series);

        DataSeries dataSeries = new DataSeries()
                .add(3, 7, 9, 1, 4, 6, 8, 2, 5);
        DataSeries subSeries = dataSeries.newSeries();

        int i = 0;
        for (String month : map.keySet()) {
            if (i>8) break;
            subSeries.add(month, map.get(month));
            i++;
        }

//        DataSeries dataSeries = new DataSeries()
//                .add(3, 7, 9, 1, 4, 6, 8, 2, 5)
//                .add(1, 3, 6, 9, 12, 15, 10, 8);

        DCharts chart = new DCharts()
                .setDataSeries(dataSeries)
                .setOptions(options)
                .show();

        return chart;
    }

    private Component drawMonthlySalesChart(Map<String, Integer> map, Map<String, Integer> yMap) {
        DataSeries dataSeries = new DataSeries();
        DataSeries subSeries = dataSeries.newSeries();
        for (String month : map.keySet()) {
            subSeries.add(month, map.get(month));
        }

        Axes axes = new Axes()
                .addAxis(
                        new XYaxis()
                                .setRenderer(AxisRenderers.DATE)
                                .setTickOptions(
                                        new AxisTickRenderer()
                                                .setFormatString("%#m/%#d/%y"))
                                .setNumberTicks(4))
                .addAxis(
                        new XYaxis(XYaxes.Y)
                                .setTickOptions(
                                        new AxisTickRenderer()
                                                .setFormatString("$%.2f")));

        Cursor cursor = new Cursor()
                .setShow(true);

        Options options = new Options()
//                .addOption(seriesDefaults)
                .addOption(axes)
//                .addOption(highlighter)
                .setAnimate(true)
                .setTitle("Sales over time (months)")
                .addOption(cursor);

        DCharts chart = new DCharts()
                .setDataSeries(dataSeries)
                .setOptions(options)
                .show();

        return chart;
    }

    private Component monthlySalesChart(final Map<String, Integer> yMap) {
        DataSeries ds = new DataSeries();
        DataSeries ds1 = ds.newSeries();
        for (String year : yMap.keySet()) {
            ds1.add(year, yMap.get(year));
        }

        Axes axes = new Axes()
                .addAxis(
                        new XYaxis()
                                .setRenderer(AxisRenderers.DATE)
                                .setTickOptions(
                                        new AxisTickRenderer()
                                                .setFormatString("%#m/%#d/%y"))
                                .setNumberTicks(4))
                .addAxis(
                        new XYaxis(XYaxes.Y)
                                .setTickOptions(
                                        new AxisTickRenderer()
                                                .setFormatString("$%.2f")));

        Cursor cursor = new Cursor()
                .setShow(true);

        Options options = new Options()
                .addOption(axes)
                .setAnimate(true)
                .setTitle("Annual sales trend")
                .addOption(cursor);

        DCharts chart = new DCharts()
                .setDataSeries(ds)
                .setOptions(options)
                .show();

        return chart;
    }

    public List<ModelA> buildModel() {
        List<ModelA> models = new ArrayList<ModelA>();
        try {

            File f = new File(fileLocation);
            System.out.println("Path: " + f.getAbsolutePath());
            FileInputStream file = new FileInputStream(new File(fileLocation));

            //Get the workbook instance for XLS file
            HSSFWorkbook workbook = new HSSFWorkbook(file);

            //Get first sheet from the workbook
            HSSFSheet sheet = workbook.getSheetAt(0);

            List<List<String>> rows = new ArrayList<List<String>>();
            //Get iterator to all the rows in current sheet
            Iterator<Row> rowIterator = sheet.iterator();
            Row headerRow = sheet.getRow(0);
            while(rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if (row == headerRow) {
                    continue;
                }

                models.add(toModelA(row));
            }

            System.out.println("Found " + models.size() + " models. Printing...");
            for (ModelA model : models) {
                System.out.println(model);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return models;
    }

    private static ModelA toModelA(Row row) throws ParseException {
        List<Object> rowList = new ArrayList<Object>();
        Iterator<Cell> cellIterator = row.cellIterator();
        List<String> l = new ArrayList<String>();
        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();
            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_BOOLEAN: {
                    //rowList.add(Boolean.toString(cell.getBooleanCellValue()));
                    break;
                }
                case Cell.CELL_TYPE_ERROR: {
                    //rowList.add(cell.getErrorCellValue());
                    break;
                }
                case Cell.CELL_TYPE_FORMULA: {
                    rowList.add(cell.getCellFormula());
                    break;
                }
                case Cell.CELL_TYPE_NUMERIC: {
                    rowList.add(cell.getNumericCellValue());
                    break;
                }
                case Cell.CELL_TYPE_STRING: {
                    rowList.add(cell.getStringCellValue());
                    break;
                }
                default: {
                    rowList.add(cell.getStringCellValue());
                }
            }
        }

        return new ModelA(rowList);
    }

}