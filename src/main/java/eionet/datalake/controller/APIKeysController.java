package eionet.datalake.controller;

import eionet.datalake.dao.APIKeyService;
import eionet.datalake.model.APIKey;
import eionet.datalake.util.BreadCrumbs;
import eionet.datalake.util.UniqueId;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * API key managing controller.
 */
@Controller
@RequestMapping("/apikeys")
public class APIKeysController {

    /**
     * Service for api key management.
     */
    @Autowired
    APIKeyService apiKeyService;

    /**
     * View of all keys.
     *
     * @param model - contains attributes for the view
     * @param message
     * @return view name
     */
    @RequestMapping({"", "/", "/view"})
    public String viewKeys(Model model, @RequestParam(required = false) String message) {
        BreadCrumbs.set(model, "API Keys");
        model.addAttribute("allAPIKeys", apiKeyService.getAll());
        APIKey apikey = new APIKey();
        apikey.setIdentifier(UniqueId.generateAPIKey());
        apikey.setKeyValue(UniqueId.generateAPIKey());
        model.addAttribute("newapikey", apikey);
        if(message != null) model.addAttribute("message", message);
        return "apikeys";
    }

    /**
     * Adds new apikey to database.
     * @param apikey apikey name
     * @param redirectAttributes
     * @return view name or redirection
     */
    @RequestMapping("/add")
    public String addAPIKey(APIKey newapikey, RedirectAttributes redirectAttributes) {
        String identifier = newapikey.getIdentifier();
        if (identifier.trim().equals("")) {
            redirectAttributes.addFlashAttribute("message", "API Key's identifier cannot be empty");
            return "redirect:view";
        }
        if ("".equals(newapikey.getKeyValue().trim())) {
            redirectAttributes.addFlashAttribute("message", "API Key's value cannot be empty");
            return "redirect:view";
        }
        apiKeyService.insert(newapikey);
        redirectAttributes.addFlashAttribute("message", "Key " + identifier + " added");
        return "redirect:view";
    }


    /**
     * Form for editing existing API key.
     * @param identifier
     * @param model - contains attributes for the view
     * @param message
     * @return view name
     */
    @RequestMapping("/edit")
    public String editAPIKeyForm(@RequestParam String identifier, Model model,
            @RequestParam(required = false) String message) {
        model.addAttribute("identifier", identifier);
        BreadCrumbs.set(model, "Modify API key");
        APIKey apikey = apiKeyService.getById(identifier);
        model.addAttribute("apikey", apikey);
        if (message != null) model.addAttribute("message", message);
        return "apikeyEdit";
    }

    /**
     * Save API key record to database.
     *
     * @param apikey
     * @param bindingResult
     * @param model - contains attributes for the view
     * @return view name
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public String editAPIKey(APIKey apikey, BindingResult bindingResult, ModelMap model) {
        apiKeyService.update(apikey);
        model.addAttribute("message", "API Key " + apikey.getIdentifier() + " updated");
        return "redirect:view";
    }

    /**
     * Deletes API key.
     *
     * @param identifier
     * @param model - contains attributes for the view
     * @return view name
     */
    @RequestMapping("/delete")
    public String deleteAPIKey(@RequestParam String identifier, Model model) {
        apiKeyService.deleteById(identifier);
        model.addAttribute("message", "API Key " + identifier + " deleted ");
        return "redirect:view";
    }
}
