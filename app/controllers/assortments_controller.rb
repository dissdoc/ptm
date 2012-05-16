class AssortmentsController < ApplicationController
  before_filter :authenticate_user!

  def index
    @title_page = 'My collections'
    add_breadcrumb @title_page, ''
  end
end