package weibo4j.apps.excel;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import weibo4j.Users;
import weibo4j.Weibo;
import weibo4j.apps.datastore.AccountStore;
import weibo4j.apps.datastore.AdsStore;
import weibo4j.apps.datastore.CommentStore;
import weibo4j.apps.datastore.HotStore;
import weibo4j.apps.datastore.ReferenceStore;
import weibo4j.apps.pojo.Account;
import weibo4j.apps.pojo.Ads;
import weibo4j.apps.pojo.Comment;
import weibo4j.apps.pojo.Reference;
import weibo4j.model.User;
import weibo4j.model.WeiboException;

public class ExcelParser {
	
	public void parse(HSSFWorkbook excel) {
		AccountStore accountStore = new AccountStore();
		ArrayList<Account> accounts = new ArrayList<Account>();
		HSSFSheet accountSheet = excel.getSheetAt(0);
		for (int NumOfRow = 1; NumOfRow <= accountSheet.getLastRowNum(); NumOfRow++) {
			if (null != accountSheet.getRow(NumOfRow)) {
				HSSFRow row = accountSheet.getRow(NumOfRow);
				Account account = convertRow2Account(row);
				accounts.add(account);
			}
		}
		accountStore.createBatchAccounts(accounts);

		ArrayList<Account> as = accountStore.getAllAccounts();
		Collections.shuffle(as);
		String accessToken = as.get(0).getAccessToken();
		Weibo weibo = new Weibo();
		weibo.setToken(accessToken);
		
		ArrayList<Reference> references = new ArrayList<Reference>();
		HSSFSheet referenceSheet = excel.getSheetAt(1);
		for (int NumOfRow = 1; NumOfRow <= referenceSheet.getLastRowNum(); NumOfRow++) {
			if (null != referenceSheet.getRow(NumOfRow)) {
				HSSFRow row = referenceSheet.getRow(NumOfRow);
				Reference reference = convertRow2Reference(row);
				references.add(reference);
			}
		}
		ReferenceStore referenceStore = new ReferenceStore();
		referenceStore.createBatchReferences(references);
		/*
		ArrayList<Ads> adss = new ArrayList<Ads>();
		HSSFSheet adsSheet = excel.getSheetAt(2);
		for (int NumOfRow = 1; NumOfRow <= adsSheet.getLastRowNum(); NumOfRow++) {
			if (null != adsSheet.getRow(NumOfRow)) {
				HSSFRow row = adsSheet.getRow(NumOfRow);
				Ads ads = new Ads();
				for (int cellNumOfRow = 0; cellNumOfRow <= row.getLastCellNum(); cellNumOfRow++) {
					if (null != row.getCell(cellNumOfRow)) {
						HSSFCell cell = row.getCell(cellNumOfRow);
						if(cellNumOfRow == 0){
							ads.setStatusId(getStrFromCell(cell));
						}else if(cellNumOfRow == 1){
							ads.setType(getStrFromCell(cell));
						}else{
							ads.setTarget(getStrFromCell(cell));
						}
					}
				}
				adss.add(ads);
			}
		}
		AdsStore adsStore = new AdsStore();
		adsStore.createBatchAds(adss);
		
		ArrayList<Comment> comments = new ArrayList<Comment>();
		HSSFSheet commentSheet = excel.getSheetAt(3);
		for (int NumOfRow = 1; NumOfRow <= commentSheet.getLastRowNum(); NumOfRow++) {
			if (null != commentSheet.getRow(NumOfRow)) {
				HSSFRow row = commentSheet.getRow(NumOfRow);
				Comment comment = new Comment();
				for (int cellNumOfRow = 0; cellNumOfRow <= row.getLastCellNum(); cellNumOfRow++) {
					if (null != row.getCell(cellNumOfRow)) {
						HSSFCell cell = row.getCell(cellNumOfRow);
						if(cellNumOfRow == 0){
							comment.setComment(getStrFromCell(cell));
						}else{
							comment.setType(getStrFromCell(cell));
						}
					}
				}
				comments.add(comment);
			}
		}
		CommentStore commentStore = new CommentStore();
		commentStore.createBatchComments(comments);
		
		ArrayList<Account> hots = new ArrayList<Account>();
		HSSFSheet hotSheet = excel.getSheetAt(4);
		for (int NumOfRow = 1; NumOfRow <= hotSheet.getLastRowNum(); NumOfRow++) {
			if (null != hotSheet.getRow(NumOfRow)) {
				HSSFRow row = hotSheet.getRow(NumOfRow);
				Account account = new Account();
				for (int cellNumOfRow = 0; cellNumOfRow <= row.getLastCellNum(); cellNumOfRow++) {
					if (null != row.getCell(cellNumOfRow)) {
						HSSFCell cell = row.getCell(cellNumOfRow);
						if(cellNumOfRow == 0){
							account.setUid(getStrFromCell(cell));
						}else if(cellNumOfRow == 1){
							account.setName(getStrFromCell(cell));
						}else if(cellNumOfRow == 2){
							account.setTarget(getStrFromCell(cell));
						}
					}
				}
				hots.add(account);
			}
		}
		HotStore hotStore = new HotStore();
		hotStore.createBatchHots(hots);
		*/
	}
	
	public void handleReference(Reference reference){
		String rname = reference.getRname();
		Users um = new Users();
		try {
			User rUser = um.showUserByScreenName(rname);
			String rid = rUser.getId();
			int followersCount = rUser.getFollowersCount();
			reference.setRid(rid);
			reference.setFollowersCount(followersCount);
		} catch (WeiboException e) {
			System.out.println(rname);
		}
	}

	public Account convertRow2Account(HSSFRow row){
		Account account = new Account();
		for (int cellNumOfRow = 0; cellNumOfRow <= row.getLastCellNum(); cellNumOfRow++) {
			if (null != row.getCell(cellNumOfRow)) {
				HSSFCell cell = row.getCell(cellNumOfRow);
				
				setField4Account(account, getStrFromCell(cell), cellNumOfRow);
			}
		}
		return account;
	}
	
	public void setField4Account(Account account, String value, int cellNum){
		if(cellNum == 0){
			account.setUid(value);
		}else if(cellNum == 1){
			account.setLoginName(value);
		}else if(cellNum == 2){
			account.setName(value);
		}else if(cellNum == 3){
			account.setPassword(value);
		}else if(cellNum == 4){
			account.setAccessToken(value);
		}else if(cellNum == 5){
			account.setIsActive(value);
		}else if(cellNum == 6){
			account.setGroup(value);
		}else if(cellNum == 7){
			account.setTarget(value);
		}
	}
	
	public Reference convertRow2Reference(HSSFRow row){
		Reference reference = new Reference();
		for (int cellNumOfRow = 0; cellNumOfRow <= row.getLastCellNum(); cellNumOfRow++) {
			if (null != row.getCell(cellNumOfRow)) {
				HSSFCell cell = row.getCell(cellNumOfRow);
				setField4Reference(reference, getStrFromCell(cell), cellNumOfRow);
			}
		}
		return reference;
	}
	
	public void setField4Reference(Reference reference, String value, int cellNum){
		if(cellNum == 0){
			reference.setUid(value);
		}else if(cellNum == 1){
			reference.setRid(value);
		}else if(cellNum == 2){
			reference.setUname(value);
		}else if(cellNum == 3){
			reference.setRname(value);
		}else if(cellNum == 4){
			reference.setTag(value);
		}else if(cellNum == 5){
			reference.setFollowersCount(Integer.parseInt(value));
		}
	}
	
	private String getStrFromCell(HSSFCell cell){
		int cellType = cell.getCellType();
		String strCell = "";
		switch (cellType) {
		case 0:// Numeric
			DecimalFormat df = new DecimalFormat("#");
			strCell = df.format(cell.getNumericCellValue());
			break;
		case 1:// String
			strCell = cell.getStringCellValue();
			break;
		default:
			
		}
		return strCell;
	}
}
