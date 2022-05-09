package jpa.basic.example.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Getter;
import lombok.Setter;

//@Entity
@Getter
@Setter
public class OrderItem {
	
	@Id @GeneratedValue
	@Column(name = "order_item_id")
	private Long id;
	
//	@Column(name = "order_id")
//	private Long orderId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id")
	private Order order;
	
//	@Column(name = "item_id")
//	private Long itemId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "item_id")
	private Item item;
	
	private int orderPrice;
	private int count;
}
