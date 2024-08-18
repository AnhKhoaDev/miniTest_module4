package com.codegym.car.controller;

import com.codegym.car.model.dto.CarDTO;
import com.codegym.car.model.entity.Car;
import com.codegym.car.service.ICarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/cars")
@PropertySource("classpath:upload_file.properties")
public class CarController {

    @Autowired
    private ICarService carService;

    @GetMapping("")
    public String index(Model model) {
        List<Car> cars = carService.findAll();
        model.addAttribute("cars", cars);
        return "/index";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("car", new Car());
        return "/create";
    }

    @Value("${file-upload}")
    private String upload;

    @PostMapping("/create")
    public String save(CarDTO carDTO) {
        MultipartFile file = carDTO.getImg();
        String fileName = file.getOriginalFilename();
        try {
            FileCopyUtils.copy(carDTO.getImg().getBytes(), new File(upload + fileName));
        }catch (IOException e){
            throw new RuntimeException(e);
        }
        Car car = new Car();
        car.setCode(carDTO.getCode());
        car.setName(carDTO.getName());
        car.setProducer(carDTO.getProducer());
        car.setPrice(carDTO.getPrice());
        car.setDescription(carDTO.getDescription());
        car.setImg(fileName);
        carService.save(car);
        return "redirect:/cars";
    }

    @GetMapping("/{id}/update")
    public String update(@PathVariable Long id, Model model) {
        model.addAttribute("car", carService.findById(id));
        return "/update";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute CarDTO carDTO) {
        MultipartFile file = carDTO.getImg();
        String fileName = file.getOriginalFilename();

        // Kiểm tra nếu file không trống
        if (!file.isEmpty()) {
            try {
                // Lưu file vào đường dẫn chỉ định
                FileCopyUtils.copy(file.getBytes(), new File(upload + fileName));
            } catch (IOException e) {
                throw new RuntimeException("Error while saving file: " + e.getMessage(), e);
            }
        } else {
            // Xử lý khi người dùng không chọn ảnh mới
            fileName = carService.findById(carDTO.getId()).getImg();
        }

        // Tạo đối tượng Car từ CarDTO
        Car car = new Car();
        car.setId(carDTO.getId()); // Đảm bảo ID được set để cập nhật đúng đối tượng
        car.setCode(carDTO.getCode());
        car.setName(carDTO.getName());
        car.setProducer(carDTO.getProducer());
        car.setPrice(carDTO.getPrice());
        car.setDescription(carDTO.getDescription());
        car.setImg(fileName);

        // Cập nhật đối tượng car vào database
        carService.update(car.getId(), car);

        return "redirect:/cars";
    }


    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id, Model model) {
        model.addAttribute("car", carService.findById(id));
        return "/delete";
    }

    @PostMapping("/delete")
    public String delete(Car car,
                         RedirectAttributes redirect) {
        carService.remove(car.getId());
        redirect.addFlashAttribute("success", "Removed car successfully!");
        return "redirect:/cars";
    }

    @GetMapping("/{id}/view")
    public String view(@PathVariable Long id, Model model) {
        model.addAttribute("car", carService.findById(id));
        return "/view";
    }

    @GetMapping("search")
    public String search(@RequestParam String name, Model model) {
        List<Car> cars = carService.findCarByName(name);
        model.addAttribute("cars",cars);
        return "/index";
    }
}
