package net.doohad.viewmodels.rev;

public class RevChannelPlItem {

	// 일련번호 인덱스
	private int idx;
	
	// 채널 광고 항목
	private RevChanAdItem adItem;
	
	
	public RevChannelPlItem(int idx, RevChanAdItem adItem) {
		this.idx = idx;
		this.adItem = adItem;
	}


	public int getIdx() {
		return idx;
	}

	public void setIdx(int idx) {
		this.idx = idx;
	}

	public RevChanAdItem getAdItem() {
		return adItem;
	}

	public void setAdItem(RevChanAdItem adItem) {
		this.adItem = adItem;
	}
}
