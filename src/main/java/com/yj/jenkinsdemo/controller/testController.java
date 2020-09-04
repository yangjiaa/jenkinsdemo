package com.yj.jenkinsdemo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Controller
public class testController {
    @RequestMapping(value = {"", "/index"})
    public String index(Model model) {
        model.addAttribute("msg", "hello");
        ThreadLocal local=new ThreadLocal();
        return "index";
    }
    public static void main(String[] args) throws IOException {
        /*FileWriter out = new FileWriter(new File("D://orders.txt"));
        for (int i = 0; i < 100000; i++) {
            out.write(
                    "vaule1,vaule2,vaule3,vaule4,vaule5,vaule6,vaule7,vaule8,vaule9,vaule10,vaule11,vaule12,vaule13,vaule14,vaule15,vaule16,vaule17,vaule18");
            out.write(System.getProperty("line.separator"));
        }
        out.close();*/
        int[ ] num = {23,45,17,11,13,89,72,26,3,17,11,13};
        quickSort(num);
    }

    public static void quickSort(int[] arr) {
        qsort(arr, 0, arr.length - 1);
    }

    private static void qsort(int[] arr, int low, int high) {
        if (low < high) {
            int pivot = partition(arr, low, high);        // 将数组分为两部分
            qsort(arr, low, pivot - 1);                   // 递归排序左子数组
            qsort(arr, pivot + 1, high);                  // 递归排序右子数组
        }
    }

    private static int partition(int[] arr, int low, int high) {
        int pivot = arr[low];               // 枢轴记录
        while (low < high) {
            while (low < high && arr[high] >= pivot) --high;
            arr[low] = arr[high];           // 交换比枢轴小的记录到左端
            while (low < high && arr[low] <= pivot) ++low;
            arr[high] = arr[low];           // 交换比枢轴小的记录到右端
        }
        // 扫描完成，枢轴到位
        arr[low] = pivot;
        // 返回的是枢轴的位置
        return low;
    }
}
