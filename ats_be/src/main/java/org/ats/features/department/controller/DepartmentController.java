package org.ats.features.department.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.ats.features.department.dto.DepartmentDto;
import org.ats.common.dto.PageResponse;
import org.ats.features.department.service.DepartmentService;
import org.ats.utils.ApiPath;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController // Spring Bean --> Container
@RequestMapping(ApiPath.DEPARTMENTS)
@RequiredArgsConstructor
@Tags({@Tag(name = "Department API", description = "The APIs/endpoints for/related the department domain")})
public class DepartmentController {
    private final DepartmentService departmentService;

    @Operation(tags = {"Department API"}, method = "GET"
            , description = "The endpoint to get a list all of department to management follow page by page"
            , parameters = {@Parameter(required = false, name = "pageIndex", description = "A selected page by user"), @Parameter(name = "pageSize", description = "Size of page")}
            ,responses = @ApiResponse(description = "", responseCode = "200", content = @Content(
                    schema =  @Schema(implementation = PageResponse.class),
            examples = {@ExampleObject(name = "totalPages", value = "10"),
                    @ExampleObject(name = "currentPage", value = "1"),
                    @ExampleObject(name = "content", value = "[{}]")
            }
    ))
    )
    @GetMapping
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<PageResponse<DepartmentDto>> getDepartment(@RequestParam(name = "pageIndex", required = false, defaultValue = "0") Integer pageIndex,
                                                                     @RequestParam(name = "pageSize", required = false, defaultValue = "5") Integer pageSize
    ) {
        PageResponse<DepartmentDto> page = departmentService.findAll(pageSize, pageIndex);

        return ResponseEntity.ok(page);
    }

    @PostMapping
    @PreAuthorize("hasRole('RECRUITER')")
    @Operation(tags = {"Department API"}, method = "GET"
            , description = "The endpoint to get a list all of department to management follow page by page"
            , requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
                    examples = {@ExampleObject(name = "DepartmentDto", value = """
                            {
                                "departmentName": "Software Engineering",
                                "description": "Detail description for the department"
                            }
                            """)},
//                            @ExampleObject(name = "departmentName", value = "Software Engineering"),
//                            @ExampleObject(name = "description", value = "Detail description for the department")},
            schema = @Schema(implementation = DepartmentDto.class)
    ))
    )
    public ResponseEntity<?> create(@RequestBody @Valid DepartmentDto departmentRequest,
                                    BindingResult bindingResult
    ) {
        departmentService.create(departmentRequest);

        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult.getFieldError().getDefaultMessage());
        }

        return ResponseEntity.ok(Map.of("message", "Create a new department successful!"));
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        departmentService.delete(id);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/public/all")
    public List<DepartmentDto> getAllDepartments() {
        return departmentService.findAll();
    }

}
