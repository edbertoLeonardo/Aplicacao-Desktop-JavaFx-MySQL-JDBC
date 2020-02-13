package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;
import model.exception.ValidationException;
import model.services.DepartmentService;

public class DepartmentFormController implements Initializable {
	
	private Department entity;
	private DepartmentService departmentService;
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

	@FXML
	private TextField textId;
	@FXML
	private TextField textName;
	@FXML
	private Label labelErrorName;
	@FXML
	private Button buttonSave;
	@FXML
	private Button buttonCancel;
	
	public void setDepartment(Department entity) {
		this.entity = entity;
	}
	
	public void setDepartmentService(DepartmentService departmentService) {
		this.departmentService = departmentService;
	}
	
	public void subscribeDataChageListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}
	
	@FXML
	public void onButtonSaveAction(ActionEvent actionEvent) {
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		if (departmentService == null) {
			throw new IllegalStateException("DepartmentService was null");
		}
		try {
		entity = getFormData();
		departmentService.saveOrUpdate(entity);
		notifyDataChageListeners();
		Utils.currentStage(actionEvent).close();
		}
		catch(ValidationException e) {
			setErrorMessages(e.getErrors());
		}
		catch(DbException e) {
			Alerts.showAlert("Error saving object", null, e.getMessage(), AlertType.ERROR);
		}
	}
	
	private void notifyDataChageListeners() {
		for (DataChangeListener dataChangeListener : dataChangeListeners) {
			dataChangeListener.onDataChange();
		}
	}

	private Department getFormData() {
		
		Department department = new Department();
		ValidationException validationException = new ValidationException("Validation error"); 
		
		department.setId(Utils.tryParseToInt(textId.getText()));
		
		if (textName.getText() == null || textName.getText().trim().equals("")) {
			validationException.addError("name", "Field can't be empty");
		}
		department.setName(textName.getText());
		
		if (validationException.getErrors().size() > 0) {
			throw validationException;
		}
		
		return department;
		
	}

	@FXML
	public void onButtonCancelAction(ActionEvent actionEvent) {
		Utils.currentStage(actionEvent).close();
	}
	
	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
			initializeNodes();
	}

	private void initializeNodes() {
		Constraints.setTextFieldInteger(textId);
		Constraints.setTextFieldMaxLength(textName, 30);
	}
	
	public void updateFormData() {
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		textId.setText(String.valueOf(entity.getId()));
		textName.setText(entity.getName());
	}
	
	private void setErrorMessages(Map<String, String> errors) {
	
		Set<String> fields = errors.keySet();
		
		if (fields.contains("name")) {
			labelErrorName.setText(errors.get("name"));
		}
	}
	
}
