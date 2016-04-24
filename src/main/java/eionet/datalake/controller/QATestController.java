package eionet.datalake.controller;

import eionet.datalake.dao.QATestService;
import eionet.datalake.model.QATestType;
import eionet.datalake.model.QATest;
import eionet.datalake.model.Authorisation;
import eionet.datalake.util.BreadCrumbs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

/**
 * QA test managing controller.
 */
@Controller
@RequestMapping("/qatests")
public class QATestController {

    /**
     * Service for QA Test storage
     */
    @Autowired
    QATestService qaTestService;

    @ModelAttribute("allQATypes")
    public List<String> allQATypes() {
        ArrayList<String> qaTypes = new ArrayList<String>();
        for (QATestType qa : QATestType.values()) {
            qaTypes.add(qa.toString());
        }
        return qaTypes;
    }

    /**
     * View for all users.
     *
     * @param model - contains attributes for the view
     * @param message
     * @return view name
     */
    @RequestMapping({"/{datasetid}", "/{datasetid}/view"})
    public String viewQATests(Model model,
            @PathVariable("datasetid") String datasetId,
            @RequestParam(required = false) String message) {
        BreadCrumbs.set(model, "QA Tests");
        model.addAttribute("datasetId", datasetId);
        model.addAttribute("qatests", qaTestService.getByDatasetId(datasetId));
        QATest newQATest = new QATest();
        newQATest.setExpectedResult("true");
        model.addAttribute("newqatest", newQATest);
        if(message != null) model.addAttribute("message", message);
        return "view_qatests";
    }

    /**
     * Adds new QA test to database. When TestId is null, then a new number is generated.
     *
     * @param qatest structure containing the QA test data, but not the datasetid.
     * @param datasetId - DatasetId
     * @param redirectAttributes
     * @return view name or redirection
     */
    @RequestMapping("/{datasetid}/add")
    public String addQATest(QATest qatest,
            @PathVariable("datasetid") String datasetId,
            RedirectAttributes redirectAttributes) {
        String testtype = qatest.getTestType();
        if (testtype.trim().equals("")) {
            redirectAttributes.addFlashAttribute("message", "test type cannot be empty");
            return "redirect:view";
        }

        qatest.setDatasetId(datasetId);
        qaTestService.save(qatest);
        redirectAttributes.addFlashAttribute("message", "QA test added");
        return "redirect:view";
    }

    /**
     * Form for editing existing QA test.
     *
     * @param testid
     * @param model - contains attributes for the view
     * @param message
     * @return view name
     */
    @RequestMapping("/{datasetid}/edit")
    public String editQATest(Model model,
            @PathVariable("datasetid") String datasetId,
            @RequestParam("testid") String testId,
            @RequestParam(required = false) String message) {
        model.addAttribute("datasetId", datasetId);
        BreadCrumbs.set(model, "Modify QA Test");
        QATest qatest = qaTestService.getById(testId);
        model.addAttribute("qatest", qatest);

        if (message != null) model.addAttribute("message", message);
        return "qatest_edit";
    }

    /**
     * Save user record to database.
     *
     * @param user
     * @param bindingResult
     * @param model - contains attributes for the view
     * @return view name
     */
    @RequestMapping(value = "/{datasetid}/edit", method = RequestMethod.POST)
    public String editUser(
            QATest qatest,
            BindingResult bindingResult, ModelMap model) {
        qaTestService.save(qatest);
        model.addAttribute("message", "Test " + qatest.getTestId() + " updated");
        return "redirect:view";
    }

    /**
     * Deletes user.
     *
     * @param userName
     * @param model - contains attributes for the view
     * @return view name
     */
    @RequestMapping("/{testid}/delete")
    public String deleteUser(Model model, @PathVariable("testid") String testId) {
        qaTestService.deleteById(testId);
        model.addAttribute("message", "QA test " + testId + " deleted ");
        return "redirect:view";
    }

    /**
     * Delete files by uuid.
     *
     * @param testIds - list of tests to delete
     */
    @RequestMapping(value = "/{datasetid}/deletetests", method = RequestMethod.POST)
    public String deleteFiles(
            @PathVariable("datasetid") String datasetId,
            @RequestParam("testid") List<String> testIds,
            final RedirectAttributes redirectAttributes) {
        qaTestService.deleteTests(testIds);
        redirectAttributes.addFlashAttribute("message", "Tests(s) deleted");
        return "redirect:/qatests/" + datasetId;
    }

}
