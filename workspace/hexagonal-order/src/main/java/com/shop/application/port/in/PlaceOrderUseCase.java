package com.shop.application.port.in;

/**
 * INPUT PORT (driving port).
 *
 * The gioi ben ngoai (REST controller, CLI, test...) chi duoc phep goi vao he thong
 * qua interface nay. Day la "cua truoc" cua hexagon.
 */
public interface PlaceOrderUseCase {

    PlaceOrderResponse execute(PlaceOrderCommand command);
}
