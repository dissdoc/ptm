module ApplicationHelper

  def title_page
    base_title = brand
    if @title_page.nil?
      base_title
    else
      "#{base_title} | #{@title_page}"
    end
  end

  def brand
    "Photo Time Machine"
  end
end
