from typing import Final

DEFAULT_OPTIONS_STORAGE_FILE: Final = "options.properties"
DEFAULT_MODEL_STORAGE_FILE: Final   = "model.csv"
DEFAULT_POINT_RADIUS: Final         = "0.025"
DEFAULT_UPDATE_TIME: Final          = "0.5"
DEFAULT_ENVIRONMENT: Final          = ".temp/editor/model/configurations"
DEFAULT_DOMAIN_MAX: Final           = "10"
DEFAULT_DOMAIN_MIN: Final           = "0"
DEFAULT_RANGE_MAX: Final            = "10"
DEFAULT_RANGE_MIN: Final            = "0"
DEFAULT_BACKEND: Final              = "Qt5Agg"
DEFAULT_SHADE: Final                = "0.8"

class ApplicationOptions:
    def __init__(self, args) -> None:
        self.options_name = args.get("options_name", DEFAULT_OPTIONS_STORAGE_FILE)
        self.point_radius = float(args.get("point_radius", DEFAULT_POINT_RADIUS))
        self.color_shade  = float(args.get("shading", DEFAULT_SHADE))
        self.update_time  = float(args.get("update_time", DEFAULT_UPDATE_TIME))
        self.environment  = args.get("environment",  DEFAULT_ENVIRONMENT)
        self.model_name   = args.get("model_name", DEFAULT_MODEL_STORAGE_FILE)
        self.backend      = args.get("backend", DEFAULT_BACKEND)

class ModelOptions:
    def __init__(self, args) -> None:
        self.max_range    = float(args.get("range_max", DEFAULT_RANGE_MAX))
        self.min_range    = float(args.get("range_min", DEFAULT_RANGE_MIN))
        self.max_domain   = float(args.get("domain_max", DEFAULT_DOMAIN_MAX))
        self.min_domain   = float(args.get("domain_min", DEFAULT_DOMAIN_MIN))

        if self.max_range > self.min_range:
            temp = self.max_range
            self.max_range = self.min_range
            self.min_range = temp

        if self.max_domain > self.min_domain:
            temp = self.max_domain
            self.max_domain = self.min_domain
            self.min_domain = temp