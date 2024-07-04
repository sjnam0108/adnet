package net.doohad.models.rev;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import net.doohad.models.adc.AdcAdCreative;
import net.doohad.models.inv.InvScreen;

@Entity
@Table(name="REV_AD_SEL_CACHES")
public class RevAdSelCache {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "AD_SEL_CACHE_ID")
	private int id;
	
	// NO!! - 광고 선택 일시 == whoCreationDate
	//
	//  선택일시의 실제 값은 화면에서의 광고 종료 시간임
	//  만약 광고 선택 일시로 처리되는 코드가 있다면 변경해야 함
	//  -- 이것 확인 필요
	//
	@Column(name = "SEL_DATE", nullable = false)
	private Date selectDate;


	// WHO 컬럼들(S)
	
	//   기기에 의해 자동 생성되고, 상당한 크기가 예상되므로 WHO 컬럼 생략

	// WHO 컬럼들(E)
	
	
	// 다른 개체 연결(S)
	
	// 상위 개체: 화면
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "SCREEN_ID", nullable = false)
	private InvScreen screen;
	
	// 상위 개체: 광고/광고 소재
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "AD_CREATIVE_ID", nullable = false)
	private AdcAdCreative adCreative;
	
	// 다른 개체 연결(E)
	
	
	public RevAdSelCache() {}
	
	public RevAdSelCache(RevAdSelect adSelect, int duration) {
		
		this.screen = adSelect.getScreen();
		this.adCreative = adSelect.getAdCreative();
		
		// 광고 선택 캐쉬에서의 광고 선택 일시는 광고의 재생 시간을 고려치 않은 순수 선택 일시로 다시 회귀
		// 동일 광고주에 의해 다른 광고만으로 구성된 경우 선택될 수 없기 때문
		//this.selectDate = Util.addSeconds(adSelect.getSelectDate(), duration);
		this.selectDate = adSelect.getSelectDate();
	}

	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getSelectDate() {
		return selectDate;
	}

	public void setSelectDate(Date selectDate) {
		this.selectDate = selectDate;
	}

	public InvScreen getScreen() {
		return screen;
	}

	public void setScreen(InvScreen screen) {
		this.screen = screen;
	}

	public AdcAdCreative getAdCreative() {
		return adCreative;
	}

	public void setAdCreative(AdcAdCreative adCreative) {
		this.adCreative = adCreative;
	}

}
