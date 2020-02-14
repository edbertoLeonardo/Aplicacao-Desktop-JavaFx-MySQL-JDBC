package model.services;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.SellerDao;
import model.entities.Seller;

public class SellerService {

	private SellerDao departmentDao = DaoFactory.createSellerDao();

	public List<Seller> findAll() {
		return departmentDao.findAll();
	}

	public void saveOrUpdate(Seller department) {
		if (department.getId() == null) {
			departmentDao.insert(department);
		} else {
			departmentDao.update(department);
		}
	}
	
	public void remove(Seller department) {
		departmentDao.deleteById(department.getId());
	}
}
