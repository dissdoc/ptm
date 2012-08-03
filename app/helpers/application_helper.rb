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

  def facebook_like
    content_tag :iframe, nil, :src => "http://www.facebook.com/plugins/like.php?href=#{CGI::escape(request.url)}&layout=standard&show_faces=true&width=450&action=like&font=arial&colorscheme=light&height=80", :scrolling => 'no', :frameborder => '0', :allowtransparency => true, :id => :facebook_like
  end

  def dt_set_defaults(item, params_name)
    render :partial => 'timelines/shared/dt_select',
           :locals => { :params_name => params_name, :generate => item }
  end
end
