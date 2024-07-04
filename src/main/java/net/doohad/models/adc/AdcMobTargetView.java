package net.doohad.models.adc;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

@Entity
@Immutable
@Subselect(
	"SELECT mt.mob_target_id as id, mt.filter_type as filter_type, mt.mob_type as mob_type, " +
			"mt.tgt_id as tgt_id, mt.sibling_seq as sibling_seq, a.ad_id, " +
			"IF(mob_type = 'RG', mr.name, rr.name) as tgt_name, " +
			"IF(mob_type = 'RG', mr.active_status, rr.active_status) as active_status " +
	"FROM adc_ads a, adc_mob_targets mt " +
	"LEFT OUTER JOIN fnd_mob_regions mr ON mr.region_id = mt.tgt_id AND mt.mob_type = 'RG' " +
	"LEFT OUTER JOIN org_rad_regions rr ON rr.rad_region_id = mt.TGT_ID AND mt.mob_type = 'CR' " +
	"WHERE mt.ad_id = a.ad_id "
)
public class AdcMobTargetView {
	// 광고 모바일 타겟 id와 동일
	@Id
	@Column(name = "ID")
	private int id;

	// 광고 id
	@Column(name = "AD_ID")
	private int adId;

	// 필터 유형
	//
	//   A		And
	//   O		Or
	//
	@Column(name = "FILTER_TYPE")
	private String filterType = "O";
	
	// 모바일 유형
	//
	//   RG      모바일 타겟팅 지역 - ReGion
	//   CR      원 반경 지역 - Circle Radius region
	//
	@Column(name = "MOB_TYPE")
	private String mobType = "";

	// 대상 id(RG, CR 등의 id)
	@Column(name = "TGT_ID")
	private int tgtId;
	
	// 대상 이름
	@Column(name = "TGT_NAME")
	private String tgtName = "";
	
	// 동일 광고에서 순서
	@Column(name = "SIBLING_SEQ")
	private int siblingSeq = 0;

	// 활성화 여부
	@Column(name = "ACTIVE_STATUS")
	private boolean activeStatus = false; 
	
	
	public AdcMobTargetView() {}

	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getAdId() {
		return adId;
	}

	public void setAdId(int adId) {
		this.adId = adId;
	}

	public String getFilterType() {
		return filterType;
	}

	public void setFilterType(String filterType) {
		this.filterType = filterType;
	}

	public String getMobType() {
		return mobType;
	}

	public void setMobType(String mobType) {
		this.mobType = mobType;
	}

	public int getTgtId() {
		return tgtId;
	}

	public void setTgtId(int tgtId) {
		this.tgtId = tgtId;
	}

	public String getTgtName() {
		return tgtName;
	}

	public void setTgtName(String tgtName) {
		this.tgtName = tgtName;
	}

	public int getSiblingSeq() {
		return siblingSeq;
	}

	public void setSiblingSeq(int siblingSeq) {
		this.siblingSeq = siblingSeq;
	}

	public boolean isActiveStatus() {
		return activeStatus;
	}

	public void setActiveStatus(boolean activeStatus) {
		this.activeStatus = activeStatus;
	}

}
