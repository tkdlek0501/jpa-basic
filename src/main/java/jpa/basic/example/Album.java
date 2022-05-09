package jpa.basic.example;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.springframework.util.Assert;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@DiscriminatorValue("A") // 기본적으로 dtype에는 Entity 명이 들어가지만 이렇게 직접 설정해줄 수도 있다
public class Album extends Item{
	
	private String artist;
	
//	static Album createAlbum(
//			String name,
//			int price,
//			String artist
//			) {
//		Album album = new Album();
//		album.setName(name);
//		album.setPrice(price);
//		album.setArtist(artist);
//		return album;
//	}
	
	@Builder(builderClassName = "createBuilder", builderMethodName = "createBuilder")
	public Album(
			String name,
			int price,
			String artist
			) {
		Assert.notNull(name, "상품 이름은 필수 항목입니다.");
		Assert.notNull(price, "상품 가격은 필수 항목입니다.");
		Assert.notNull(artist, "앨범 가수 이름은 필수 항목입니다.");
		
		this.setName(name);
		this.setPrice(price);
		this.artist = artist;
	}
	
}

