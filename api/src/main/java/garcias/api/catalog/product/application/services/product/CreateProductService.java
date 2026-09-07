package garcias.api.catalog.product.application.services.product;

import garcias.api.catalog.product.application.dto.requests.CreateProductRequest;
import garcias.api.catalog.product.application.storage.ImageStorage;
import garcias.api.catalog.product.application.usecases.product.CreateProductUseCase;
import garcias.api.catalog.product.application.validation.WebpImageValidator;
import garcias.api.catalog.category.domain.valueobjects.CategoryId;
import garcias.api.catalog.product.domain.entities.Product;
import garcias.api.catalog.product.domain.repositories.ProductRepository;
import garcias.api.catalog.product.domain.valueobjects.CatalogPosition;
import garcias.api.catalog.product.domain.valueobjects.ProductName;
import garcias.api.catalog.product.domain.valueobjects.ProductPhoto;
import garcias.api.catalog.product.domain.valueobjects.ProductWeight;
import org.springframework.context.ApplicationEventPublisher;
import garcias.api.catalog.category.domain.persistence.CategoryRepository;
import garcias.api.shared.exceptions.ObjectNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateProductService implements CreateProductUseCase {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ImageStorage imageStorage;
    private final WebpImageValidator webpImageValidator;


    public CreateProductService(
            ProductRepository productRepository,
            CategoryRepository categoryRepository,
            ApplicationEventPublisher publisher,
            WebpImageValidator webpImageValidator,
            ImageStorage imageStorage
    ) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.imageStorage = imageStorage;
        this.webpImageValidator = webpImageValidator;
    }


    @Override
    @Transactional
    public Product execute(CreateProductRequest request) {

        String imagePath = null;

        try {

            CategoryId categoryId =
                    new CategoryId(request.categoryId());

            categoryRepository
                    .findById(categoryId)
                    .orElseThrow(() ->
                            new ObjectNotFoundException(request.categoryId())
                    );

            webpImageValidator.validate(request.photo());


            imagePath = imageStorage.save(request.photo());


            CatalogPosition position =
                    productRepository
                            .findLastPosition()
                            .map(CatalogPosition::next)
                            .orElse(CatalogPosition.first());


            Product product =
                    Product.create(
                            new ProductName(request.name()),
                            new ProductWeight(request.weight()),
                            position,
                            new ProductPhoto(imagePath),
                            categoryId
                    );


            return productRepository.save(product);

        } catch (Exception exception) {

            if (imagePath != null) {
                imageStorage.delete(imagePath);
            }

            throw exception;
        }
    }
}