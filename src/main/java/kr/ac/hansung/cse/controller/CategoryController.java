package kr.ac.hansung.cse.controller;

import jakarta.validation.Valid;
import kr.ac.hansung.cse.exception.DuplicateCategoryException;
import kr.ac.hansung.cse.model.CategoryForm;
import kr.ac.hansung.cse.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    // 1. 카테고리 목록 조회
    @GetMapping
    public String listCategories(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        return "categoryList"; // categoryList.html로 이동
    }

    // 2. 등록 폼 표시
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("categoryForm", new CategoryForm());
        return "categoryForm"; // categoryForm.html로 이동
    }

    // 3. 등록 처리
    @PostMapping("/create")
    public String createCategory(@Valid @ModelAttribute CategoryForm categoryForm,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {
        
        // Bean Validation 오류 (공백, 길이 제한 등) 체크
        if (bindingResult.hasErrors()) {
            return "categoryForm";
        }

        try {
            categoryService.createCategory(categoryForm.getName());
            redirectAttributes.addFlashAttribute("successMessage", "카테고리가 등록되었습니다.");
        } catch (DuplicateCategoryException e) {
            // 중복 오류 발생 시 에러 메시지를 폼에 전달
            bindingResult.rejectValue("name", "duplicate", e.getMessage());
            return "categoryForm";
        }

        return "redirect:/categories";
    }

    // 4. 삭제 처리
    @PostMapping("/{id}/delete")
    public String deleteCategory(@PathVariable Long id, 
                               RedirectAttributes redirectAttributes) {
        try {
            categoryService.deleteCategory(id);
            redirectAttributes.addFlashAttribute("successMessage", "카테고리가 삭제되었습니다.");
        } catch (IllegalStateException e) {
            // 상품이 연결되어 삭제 실패한 경우 에러 메시지 전달
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/categories";
    }
}