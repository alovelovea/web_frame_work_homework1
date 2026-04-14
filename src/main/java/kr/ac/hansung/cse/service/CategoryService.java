package kr.ac.hansung.cse.service;

import kr.ac.hansung.cse.exception.DuplicateCategoryException;
import kr.ac.hansung.cse.model.Category;
import kr.ac.hansung.cse.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 생성합니다 (Lombok)
public class CategoryService {

    private final CategoryRepository categoryRepository;

    /**
     * 모든 카테고리 조회
     */
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    /**
     * 새 카테고리 등록
     * @Transactional: 쓰기 작업이므로 readOnly = false로 설정
     */
    @Transactional
    public void createCategory(String name) {
        // [비즈니스 로직] 이름 중복 검사
        categoryRepository.findByName(name)
                .ifPresent(c -> { 
                    throw new DuplicateCategoryException(name); 
                });
        
        categoryRepository.save(new Category(name));
    }

    /**
     * 카테고리 삭제
     * [비즈니스 규칙] 연결된 상품이 있으면 삭제 불가
     */
    @Transactional
    public void deleteCategory(Long id) {
        // 해당 카테고리를 참조하는 상품이 몇 개인지 확인
        long count = categoryRepository.countProductsByCategoryId(id);
        
        if (count > 0) {
            throw new IllegalStateException("상품 " + count + "개가 연결되어 있어 삭제할 수 없습니다.");
        }
        
        categoryRepository.delete(id);
    }
}