package evm.dmc.web.algo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import evm.dmc.api.model.AlgorithmModel;
import evm.dmc.config.ViewsConfig;
import evm.dmc.core.api.Project;
import evm.dmc.web.config.annotations.DefaultProject;

@Controller
@RequestMapping("/{userId}/${views.createalg}")
//@RequestMapping("/{userId}/createalg")
public class CreateAlgorithmController {
	Project project;

	@Value("${views.createalg}")
	String createView /*= "createalg"*/;
	
	public CreateAlgorithmController(@Autowired @DefaultProject Project project) {
		this.project = project;		
	}
	
	public void setProject(Project project){
		this.project = project;
	}
	
	@GetMapping
	String createAlgorithm(@PathVariable String userId, Model model) {
		AlgorithmModel algModel = project.createAlgorithm().getModel();
		model.addAttribute("algModel", algModel);
		model.addAttribute("view", createView);
		return "jsp/" + createView;
	}

}
