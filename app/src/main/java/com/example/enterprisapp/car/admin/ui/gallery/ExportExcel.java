package com.example.enterprisapp.car.admin.ui.gallery;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import com.example.enterprisapp.car.Model.Request;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ExportExcel {

    String[] status_text = {"PENDING","ACCEPTED","DECLINED","DONE", "TRIP STARTED", "CANCELED BY ADMIN", "CANCELED BY USER"};



    private static Cell cell, cell2, cell3;

    public void RequestCustomers(Context context, List<Request> requestsList, String fileName){
        Workbook workbook = new HSSFWorkbook();
        Sheet sheetCustomer = workbook.createSheet("Car Request Report"); //Creating a sheet

        Row topicRow = sheetCustomer.createRow(0);

        cell = topicRow.createCell(0);
        cell.setCellValue("WALLET MICROFINANCE SC \nCAR REQUEST LIST FOR\n"+fileName);


        sheetCustomer.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));
        sheetCustomer.setColumnWidth(0, 5000);
        sheetCustomer.setColumnWidth(1, 3000);
        sheetCustomer.setColumnWidth(2, 2500);
        sheetCustomer.setColumnWidth(3, 2500);
        Row headerRow = sheetCustomer.createRow(1);



        cell = headerRow.createCell(0);
        cell.setCellValue("Name");
//        cell.setCellStyle(headerCellStyle);

        cell = headerRow.createCell(1);
        cell.setCellValue("From");
//        cell.setCellStyle(headerCellStyle);

        cell = headerRow.createCell(2);
        cell.setCellValue("To");
//        cell.setCellStyle(headerCellStyle);

        cell = headerRow.createCell(3);
        cell.setCellValue("Requested On");

        cell = headerRow.createCell(4);
        cell.setCellValue("Requested For");

        cell = headerRow.createCell(5);
        cell.setCellValue("Reason");

        cell = headerRow.createCell(6);
        cell.setCellValue("Distance");

        cell = headerRow.createCell(7);
        cell.setCellValue("Status");

        cell = headerRow.createCell(8);
        cell.setCellValue("Remark");



        for(int  i=0; i<requestsList.size(); i++){

            Row rowData = sheetCustomer.createRow(i+2);
            cell = rowData.createCell(0);
            cell.setCellValue(requestsList.get(i).getNameOfEmployee());

            cell = rowData.createCell(1);
            cell.setCellValue(requestsList.get(i).getFrom());

            cell = rowData.createCell(2);
            cell.setCellValue(requestsList.get(i).getTo());

            cell = rowData.createCell(3);
            cell.setCellValue(requestsList.get(i).getRequestTime().toDate().toString());

            cell = rowData.createCell(4);
            cell.setCellValue(requestsList.get(i).getForWhen().toDate().toString());

            cell = rowData.createCell(5);
            cell.setCellValue(requestsList.get(i).getReason());

            cell = rowData.createCell(6);
            cell.setCellValue(requestsList.get(i).getDistance()+" KM");

            cell = rowData.createCell(7);
            cell.setCellValue(status_text[requestsList.get(i).getStatus()-1]);

            cell = rowData.createCell(8);
            cell.setCellValue(requestsList.get(i).getRemark());

        }



        String extStorageDirectory = Environment.getExternalStorageDirectory()
                .toString();
        File folder = new File(extStorageDirectory, "Wallet Car Report");// Name of the folder you want to keep your file in the local storage.
        folder.mkdir(); //creating the folder
        File file = new File(folder, fileName+".xls");
        if(file.exists()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            // Set the message show for the Alert time
            builder.setMessage("Are you sure u want to override it?");

            // Set Alert Title
            builder.setTitle("File already exist!");

            // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
            builder.setCancelable(false);

            // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
            builder.setPositiveButton("Yes", (dialog, which) -> {
                // When the user click yes button then app will close
                try {
                    file.createNewFile(); // creating the file inside the folder
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

                try {
                    FileOutputStream fileOut = new FileOutputStream(file); //Opening the file
                    workbook.write(fileOut); //Writing all your row column inside the file
                    fileOut.close(); //closing the file and done
                    Toast.makeText(context, fileName+".xls saved successfully", Toast.LENGTH_SHORT).show();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            // Set the Negative button with No name Lambda OnClickListener method is use of DialogInterface interface.
            builder.setNegativeButton("No", (dialog, which) -> {
                // If user click no then dialog box is canceled.
                dialog.cancel();
            });

            // Create the Alert dialog
            AlertDialog alertDialog = builder.create();
            // Show the Alert Dialog box
            alertDialog.show();
        }
        else{
            try {
                file.createNewFile(); // creating the file inside the folder
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            try {
                FileOutputStream fileOut = new FileOutputStream(file); //Opening the file
                workbook.write(fileOut); //Writing all your row column inside the file
                fileOut.close(); //closing the file and done
                Toast.makeText(context, fileName+".xls saved successfully", Toast.LENGTH_SHORT).show();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
